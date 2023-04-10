package com.no.crowd.mvc.config;

import com.google.gson.Gson;
import com.no.crowd.constant.CrowdConstant;
import com.no.crowd.exception.LoginAcctAlreadyInUseException;
import com.no.crowd.exception.LoginAcctAlreadyInUseForUpdateException;
import com.no.crowd.exception.LoginFailedException;
import com.no.crowd.util.CrowdUtil;
import com.no.crowd.util.ResultEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author NO
 * @create 2022-06-10-15:09
 */

@ControllerAdvice       //@ControllerAdvice表示当前类为一个基于注解的异常处理器
public class CrowdExceptionResolver {

    // 更新账号重复异常处理
    @ExceptionHandler(value = {LoginAcctAlreadyInUseForUpdateException.class})     //@ExceptionHandler将具体的异常与方法关联起来
    public ModelAndView resolverLoginAcctAlreadyInUseForUpdateException(LoginAcctAlreadyInUseForUpdateException exception, HttpServletRequest request , HttpServletResponse response) throws IOException {

        ModelAndView modelAndView = commonResolver("system-error", exception, request, response);
        //返回ModelAndView对象
        return modelAndView;
    }


    // 更新账号重复异常处理
    @ExceptionHandler(value = {LoginAcctAlreadyInUseException.class})     //@ExceptionHandler将具体的异常与方法关联起来
    public ModelAndView resolverLoginAcctAlreadyInUseException(LoginAcctAlreadyInUseException exception, HttpServletRequest request , HttpServletResponse response) throws IOException {

        ModelAndView modelAndView = commonResolver("admin-add", exception, request, response);
        //返回ModelAndView对象
        return modelAndView;
    }


    // 登陆失败异常处理
    @ExceptionHandler(value = {LoginFailedException.class})     //@ExceptionHandler将具体的异常与方法关联起来
    public ModelAndView resolverLoginFailedException(LoginFailedException exception, HttpServletRequest request , HttpServletResponse response) throws IOException {

        ModelAndView modelAndView = commonResolver("admin-login", exception, request, response);
        //返回ModelAndView对象
        return modelAndView;
    }


    @ExceptionHandler(value = {NullPointerException.class})     //@ExceptionHandler将具体的异常与方法关联起来
    public ModelAndView resolverNullPointerException(NullPointerException exception, HttpServletRequest request , HttpServletResponse response) throws IOException {

        ModelAndView modelAndView = commonResolver("system-error", exception, request, response);
        //返回ModelAndView对象
        return modelAndView;
    }

    private ModelAndView commonResolver(String viewName,                   //视图名称
                                        Exception exception,               //实际捕获的异常
                                        HttpServletRequest request,        // 当前请求对象
                                        HttpServletResponse response       //当前响应对象
    ) throws IOException {

        //判断请求类型
        boolean requestType = CrowdUtil.judgeRequestType(request);

        //如果是Ajax请求
        if(requestType){
            //创建ResultEntity对象
            ResultEntity<Object> resultEntity = ResultEntity.failed(exception.getMessage());

            //创建Gson对象
            Gson gson = new Gson();

            //将ResultEntity对象转化为JSON字符串
            String json = gson.toJson(resultEntity);

            //将JSON字符串作为响应体返回给浏览器
            response.getWriter().write(json);

            //由于已经通过原生的response对象进行了响应，所有不提供ModelAndView对象
            return null;
        }


        //如果不是Ajax请求，则创建ModelAndView对象
        ModelAndView modelAndView = new ModelAndView();

        //将exception对象存入modelAndView
        modelAndView.addObject(CrowdConstant.ATTR_NAME_EXCEPTION,exception);

        //设置对应的视图名称
        modelAndView.setViewName(viewName);

        return  modelAndView;

    }

}
