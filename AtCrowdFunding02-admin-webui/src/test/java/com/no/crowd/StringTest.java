package com.no.crowd;

import com.no.crowd.util.CrowdUtil;
import org.junit.Test;

/**
 * @author NO
 * @create 2022-06-12-10:23
 */
public class StringTest {

    @Test
    public void md5Test(){

        String md5 = CrowdUtil.md5("123123");

        System.out.println(md5);
    }
}
