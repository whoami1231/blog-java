package run.app.service;

import lombok.NonNull;
import run.app.entity.DTO.BaseResponse;
import run.app.entity.VO.PageInfo;
import run.app.entity.VO.LoginParams;
import run.app.entity.VO.QueryParams;
import run.app.entity.VO.RegisterParams;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/8/16 9:00
 * Description: 账户服务
 */
public interface AccountService {

    /**
    * 功能描述: 用户登陆
    * @Param: [loginParams]
    * @Return: run.app.entity.DTO.BaseResponse
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:52
     */
    @NonNull
    BaseResponse loginService(@NonNull LoginParams loginParams);


    /**
    * 功能描述: 用户更新密码
    * @Param: [oldPassword, newPassword, token]
    * @Return: boolean
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:52
     */
    BaseResponse updatePassword(@NonNull String oldPassword,@NonNull String newPassword,String token);


    /**
    * 功能描述: 根据Token获取用户名
    * @Param: [token]
    * @Return: java.lang.String
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:52
     */
    String getUsernameByToken(@NonNull String token);


    /**
    * 功能描述: 根据角色Id获取到账户名称
    * @Param: [userId]
    * @Return: java.lang.String
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:58
     */
    String getUsernameById(Long userId);

    /**
    * 功能描述: 根据账号获取到角色Id
    * @Param: [username]
    * @Return: java.lang.Long
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:58
     */
    @NonNull
    Long findBloggerIdByUsername(@NonNull String username);


    /**
    * 功能描述: 注册服务
    * @Param: [registerParams]
    * @Return: run.app.entity.DTO.BaseResponse
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:59
     */
    BaseResponse registerUser(@NonNull RegisterParams registerParams);

    /**
    * 功能描述: 更新用户状态
    * @Param: [bloggerId, status, token]
    * @Return: run.app.entity.DTO.BaseResponse
    * @Author: WHOAMI
    * @Date: 2020/1/30 17:59
     */
    BaseResponse updateUserStatus(Long bloggerId,String status,String token);

    /**
    * 功能描述: 删除用户
    * @Param: [bloggerId, token]
    * @Return: run.app.entity.DTO.BaseResponse
    * @Author: WHOAMI
    * @Date: 2020/1/30 18:00
     */
    BaseResponse deleteUser(Long bloggerId,String token);

    /**
    * 功能描述: 模糊查询用户
    * @Param: [pageNum, pageSize, queryParams]
    * @Return: run.app.entity.DTO.BaseResponse
    * @Author: WHOAMI
    * @Date: 2020/1/30 18:00
     */
    BaseResponse selectUserByExample(PageInfo pageInfo, QueryParams queryParams);


    /**
     * 功能描述: 用户登出
     * @Param: [token]
     * @Return: boolean
     * @Author: WHOAMI
     * @Date: 2020/1/30 20:13
     */
    BaseResponse logout(String token);

}
