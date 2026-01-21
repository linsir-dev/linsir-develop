package com.linsir.abc.pdai.Base.exceptiondemo;

/**
 * 自定义异常类
 * 演示如何创建自定义检查型异常
 */
public class CustomException extends Exception {
    
    public CustomException() {
        super();
    }
    
    public CustomException(String message) {
        super(message);
    }
    
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CustomException(Throwable cause) {
        super(cause);
    }
}
