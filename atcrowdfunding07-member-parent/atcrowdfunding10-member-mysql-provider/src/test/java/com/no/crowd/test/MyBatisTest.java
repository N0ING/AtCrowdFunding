package com.no.crowd.test;


import com.no.crowd.entity.po.MemberPO;
import com.no.crowd.mapper.MemberPOMapper;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MemberPOMapper memberPOMapper;

    private Logger logger =  Logger.getLogger(MyBatisTest.class);


    @Test
    public void testMapper(){

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String password = "123123";

        String encode = passwordEncoder.encode(password);

        MemberPO memberPO = new MemberPO(null, "jack", encode, "杰克", "jack@163.com", 1, 1, "杰克", "123123", 2);

        memberPOMapper.insert(memberPO);

    }


    @Test
    public void testConnection() throws SQLException {

        Connection connection = dataSource.getConnection();

        logger.debug(connection.toString());

    }

}
