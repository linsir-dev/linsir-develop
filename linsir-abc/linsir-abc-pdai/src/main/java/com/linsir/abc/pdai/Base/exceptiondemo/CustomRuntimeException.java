package com.linsir.abc.pdai.Base.exceptiondemo;

/**
 * 自定义运行时异常类
 * 演示如何创建自定义非检查型异常
 */
public class CustomRuntimeException extends RuntimeException {
    
    public CustomRuntimeException() {
        super();
    }
    
    public CustomRuntimeException(String message) {
        super(message);
    }
    
    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CustomRuntimeException(Throwable cause) {
        super(cause);
    }
}
