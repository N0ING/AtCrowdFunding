package com.no.crowd.exception;

/**
 * 自定义异常-登录失败异常
 * @author NO
 * @create 2022-06-12-9:38
 */

public class LoginFailedException extends  RuntimeException{



    public LoginFailedException() {
    }

    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailedException(Throwable cause) {
        super(cause);
    }

    public LoginFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
