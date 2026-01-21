package com.linsir.abc.pdai.Base.spidemo;

/**
 * SPI 服务接口
 * 定义服务的方法
 */
public interface HelloService {
    /**
     * 服务方法
     */
    void sayHello();
    
    /**
     * 获取服务名称
     */
    String getServiceName();
}
