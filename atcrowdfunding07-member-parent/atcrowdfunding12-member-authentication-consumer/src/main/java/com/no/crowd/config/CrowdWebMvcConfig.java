package com.no.crowd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrowdWebMvcConfig implements WebMvcConfigurer {


    public void addViewControllers(ViewControllerRegistry registry) {

        // 浏览器访问的地址
        String urlPath = "/auth/member/to/reg/page" ;

        // 目标视图的名称
        String viewName = "member-reg";

        // 前往用户注册页面
        registry.addViewController(urlPath).setViewName(viewName);

        //前往登录页面
        registry.addViewController("/auth/member/to/login/page").setViewName("member-login");

        // 前往登录完成后的用户主页面
        registry.addViewController("/auth/member/to/center/page").setViewName("member-center");
        // 前往“我的众筹”页面
        registry.addViewController("/auth/member/to/crowd/page.html").setViewName("member-crowd");

    }


}
