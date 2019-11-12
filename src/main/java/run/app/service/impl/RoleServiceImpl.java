package run.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.app.entity.enums.Role;
import run.app.entity.model.BloggerRole;
import run.app.entity.model.BloggerRoleExample;
import run.app.entity.model.BloggerRoleMapExample;
import run.app.entity.model.BloggerRoleMapKey;
import run.app.exception.ServiceException;
import run.app.mapper.BloggerRoleMapMapper;
import run.app.mapper.BloggerRoleMapper;
import run.app.service.RoleService;
import run.app.util.AppUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/10/30 22:43
 * Description: 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {


    @Autowired
    private BloggerRoleMapper bloggerRoleMapper;

    @Autowired
    private BloggerRoleMapMapper bloggerRoleMapMapper;



    @Override
    public void setRoleWithUserId(Role role, Long userId) {
        Long roleId = getRoleIdByType(role);
        bloggerRoleMapMapper.insert(new BloggerRoleMapKey(userId,roleId));
    }



    @Override
    public void addRole(BloggerRole bloggerRole) {
        bloggerRoleMapper.insert(bloggerRole);
    }

    @Override
    public Long getRoleIdByType(Role role) {
        BloggerRoleExample bloggerRoleExample = new BloggerRoleExample();
        BloggerRoleExample.Criteria criteria = bloggerRoleExample.createCriteria();
        criteria.andRoleNameEqualTo(role.getAuthority());
        return bloggerRoleMapper.selectByExample(bloggerRoleExample).stream()
                .findFirst().orElseThrow(()->new ServiceException("服务异常")).getId();
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        BloggerRoleMapExample bloggerRoleMapExample = new BloggerRoleMapExample();
        BloggerRoleMapExample.Criteria criteria = bloggerRoleMapExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return bloggerRoleMapMapper.selectByExample(bloggerRoleMapExample).stream()
                .filter(Objects::nonNull)
                .map(n->getRoleById(n.getRoleId()))
                .collect(Collectors.toList());
    }

    @Override
    public Role getRoleById(Long id) {
        return Role.valueOf(bloggerRoleMapper.selectByPrimaryKey(id).getRoleName());
    }
}