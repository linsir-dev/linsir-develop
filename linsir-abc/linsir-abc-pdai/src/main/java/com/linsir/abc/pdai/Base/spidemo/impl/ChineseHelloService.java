package com.linsir.abc.pdai.base.spidemo.impl;

import com.linsir.abc.pdai.base.spidemo.HelloService;

/**
 * 中文实现的HelloService
 */
public class ChineseHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("你好！这是中文实现的HelloService");
    }

    @Override
    public String getServiceName() {
        return "ChineseHelloService";
    }
}
