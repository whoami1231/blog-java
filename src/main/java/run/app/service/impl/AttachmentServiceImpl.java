package run.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.app.entity.DTO.*;
import run.app.entity.VO.AttachmentParams;
import run.app.entity.VO.AttachmentQueryParams;
import run.app.entity.enums.CiteNumEnum;
import run.app.entity.model.BloggerPicture;
import run.app.entity.model.BloggerPictureExample;
import run.app.exception.BadRequestException;
import run.app.exception.UnAccessException;
import run.app.mapper.BloggerPictureMapper;
import run.app.service.TokenService;
import run.app.service.ArticleService;
import run.app.service.AttachmentService;
import run.app.service.UserService;
import run.app.util.AppUtil;
import run.app.util.UploadUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/9/3 19:38
 * Description: 附件服务层实现类
 */
@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {


    @Autowired
    BloggerPictureMapper bloggerPictureMapper;


    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Autowired
    TokenService tokenService;


    @Value("${fileServer}")
    String fileServer;

    private final Integer DEFAULT_NUM = 0;


    UploadUtil uploadUtil;

    public AttachmentServiceImpl() {
        this.uploadUtil = UploadUtil.getInstance();
    }


    @Override
    public String selectPicById(Long id) {
        return bloggerPictureMapper.selectByPrimaryKey(id).getFileKey();
    }


    @Override
    public BaseResponse getAttachmentList(run.app.entity.VO.PageInfo pageInfo, AttachmentQueryParams attachmentQueryParams, String token) {

        Long id = tokenService.getUserIdWithToken(token);


        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        /**
         * 功能描述: 修改成模糊查询
         * @Author: WHOAMI
         * @Date: 2019/11/13 14:00
         */
        List<BloggerPicture> bloggerPictures = bloggerPictureMapper.selectPictureByExample(attachmentQueryParams, id);

        PageInfo<BloggerPicture> bloggerPicturePageInfo = new PageInfo<>(bloggerPictures);

        //todo:代码优化
        List<Picture> pictures = bloggerPictures.stream().filter(Objects::nonNull).map(item -> {
                    return new Picture(item.getId(), covertAttachmentPath(item.getPath()), item.getTitle());

                }
        ).collect(Collectors.toList());


        DataGrid dataGrid = new DataGrid();

        dataGrid.setTotal(bloggerPicturePageInfo.getTotal());
        dataGrid.setRows(pictures);

        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(dataGrid);

        return baseResponse;
    }

    @Override
    @Transactional
    public BaseResponse uploadAttachment(MultipartFile file, String token) {
        uploadFile(file, tokenService.getUserIdWithToken(token), null);
        return new BaseResponse(HttpStatus.OK.value(), null, "上传附件成功");
    }

    @Override
    public Long uploadFile(MultipartFile file, Long userId, String title) {
        UploadUtil instance = UploadUtil.getInstance();
        ImageFile imageFile = instance.uploadFile(file).orElseThrow(() -> new BadRequestException("用户上传图片失败"));
        BloggerPicture bloggerPicture = new BloggerPicture();

        bloggerPicture.setId(AppUtil.nextId());

        bloggerPicture.setBloggerId(userId);


        bloggerPicture.setUploadDate(new Date());

        bloggerPicture.setUpdateDate(new Date());

//        修复上传逻辑错误，开始上传的图片引用人数应该是0
        bloggerPicture.setCiteNum(DEFAULT_NUM);

        bloggerPicture.setMediaType(imageFile.getMediaType().getType());

        BeanUtils.copyProperties(imageFile, bloggerPicture);

//        判断用户是否自主赋值title
        if (!StringUtils.isBlank(title)) {
            bloggerPicture.setTitle(title);
        }

        bloggerPictureMapper.insertSelective(bloggerPicture);

        return bloggerPicture.getId();
    }


    @Override
    public Long getIdByTitle(String title) {
        BloggerPictureExample bloggerPictureExample = new BloggerPictureExample();
        BloggerPictureExample.Criteria criteria = bloggerPictureExample.createCriteria();
        criteria.andFileKeyEqualTo(title);
        List<BloggerPicture> bloggerPictures = bloggerPictureMapper.selectByExample(bloggerPictureExample);
        BloggerPicture bloggerPicture = bloggerPictures.stream().filter(Objects::nonNull).findFirst().orElseThrow(() -> new BadRequestException("图片名称有误,或附件已被删除！"));
        return bloggerPicture.getId();

    }

    @Override
    public String getTitleById(Long id) {
        return bloggerPictureMapper.selectByPrimaryKey(id).getTitle();
    }

    @Override
    public String getPathById(Long id) {
        return covertAttachmentPath(bloggerPictureMapper.selectByPrimaryKey(id).getPath());
    }


    @Override
    public String covertAttachmentPath(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append(fileServer);
        builder.append(File.separator);
        builder.append(path);
        return builder.toString();
    }

    @Override
    @Transactional
    public BaseResponse updateInfo(Long id, AttachmentParams attachmentParams, String token) {

        BloggerPicture bloggerPicture = bloggerPictureMapper.selectByPrimaryKey(id);

        tokenService.authentication(bloggerPicture.getBloggerId(), token);

        BloggerPicture bloggerPicture1 = new BloggerPicture();

        bloggerPicture1.setTitle(attachmentParams.getTitle());
        bloggerPicture1.setId(id);
        bloggerPictureMapper.updateByPrimaryKeySelective(bloggerPicture1);

        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setStatus(HttpStatus.OK.value());

        return baseResponse;
    }

    @Override
    public BaseResponse getInfo(Long id, String token) {
        BloggerPicture bloggerPicture = bloggerPictureMapper.selectByPrimaryKey(id);

        //需要处理空字符问题
        tokenService.authentication(bloggerPicture.getBloggerId(), token);

        PictureInfo pictureInfo = new PictureInfo();

        BeanUtils.copyProperties(bloggerPicture, pictureInfo);

        return new BaseResponse(HttpStatus.OK.value(), null, pictureInfo);
    }

    @Override
    @Transactional
    public BaseResponse deleteAttachment(Long id, String token) {
        BloggerPicture bloggerPicture = bloggerPictureMapper.selectByPrimaryKey(id);

        //需要处理空字符问题
        tokenService.authentication(bloggerPicture.getBloggerId(), token);

        articleService.deleteQuotePic(id);

//          todo 这里多做了一次查询
        deletePic(bloggerPicture.getId());

        return new BaseResponse(HttpStatus.OK.value(), "图片删除成功", null);
    }

    @Override
    public void deletePic(Long id) {
        BloggerPicture bloggerPicture = bloggerPictureMapper.selectByPrimaryKey(id);
        bloggerPictureMapper.deleteByPrimaryKey(id);
//        带着缩略图一起删除
        uploadUtil.delFile(bloggerPicture.getPath());

        uploadUtil.delFile(bloggerPicture.getThumbPath());
    }

    @Override
    public BaseResponse findAllMediaType(String token) {
        Long id = tokenService.getUserIdWithToken(token);

        return new BaseResponse(HttpStatus.OK.value(), null, bloggerPictureMapper.findAllMediaType(id));
    }

    @Override
    public void changePictureStatus(Long id, CiteNumEnum citeNumEnum) {
        if (citeNumEnum.getValue()) {
            bloggerPictureMapper.updatePictureByAddCiteNum(id);
        } else {
            bloggerPictureMapper.updatePictureByReduceCiteNum(id);
        }
    }


}
