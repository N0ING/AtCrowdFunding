package com.no.crowd;

import com.no.crowd.entity.Admin;
import com.no.crowd.entity.Role;
import com.no.crowd.mapper.AdminMapper;
import com.no.crowd.mapper.RoleMapper;
import com.no.crowd.service.AdminService;
import org.aspectj.apache.bcel.util.ClassPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author NO
 * @create 2022-06-07-16:42
 */
//在类上必要的注解，Spring 整合Junit
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-persist-mybatis.xml","classpath:spring-persist-tx.xml"})
public class CrowdTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testBC(){
        String encode = passwordEncoder.encode("123123");

        System.out.println(encode);
    }

    @Test
    public void testRoleSave(){
        for (int i =0;i<235;i++){
            roleMapper.insert(new Role(null, "role"+i));
        }
    }

    @Test
    public void testPage(){
        for(int i=0;i<234;i++){
            adminMapper.insert(new Admin(null,"loginAcct"+i,"userPswd"+i,"username"+i,"email"+i,null));
        }

    }

    @Test
    public void textTx(){
        Admin admin = new Admin(null,"jerry","123456","吉瑞","jerry@qq.com",null);
        adminService.saveAdmin(admin);
    }

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin(null, "tom", "123123", "汤姆", "tom@qq.com", null);
        int count = adminMapper.insert(admin);
        System.out.println(count);
    }


    @Test
    public void testConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }
}
