package run.app.ServiceTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import run.app.entity.enums.Role;
import run.app.entity.model.Blog;
import run.app.entity.model.BloggerRole;
import run.app.service.RoleService;
import run.app.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/10/30 22:52
 * Description: 角色服务控制
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RoleServiceTest {


    @Autowired
    RoleService roleService;

    AppUtil appUtil = AppUtil.getInstance();

    /**
    * 功能描述: 初始化数据库
     */
    @Test
    public void initRoleDatabase(){
        List<BloggerRole> roles = new ArrayList<BloggerRole>(){
            {
                add(new BloggerRole(387055195109982208L, Role.ADMIN.getAuthority(),"管理员"));
                add(new BloggerRole(387055486085627904L,Role.USER.getAuthority(),"用户"));
            }
        };

        roles.stream().forEach(x->roleService.addRole(x));
    }
}