package com.no.crowd.mvc.interceptor;

import com.no.crowd.constant.CrowdConstant;
import com.no.crowd.entity.Admin;
import com.no.crowd.exception.AccessForbiddenException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        //通过request对象获取Session对象
        HttpSession session = httpServletRequest.getSession();

        //尝试从Session域中获取Admin对象
        Admin admin = (Admin)session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN);

        //判断admin是否为空
        if(admin == null ){
            throw  new AccessForbiddenException();
        }

        //如果不为空，返回true放行
        return true;
    }


}
