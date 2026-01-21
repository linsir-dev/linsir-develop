package com.linsir.abc.pdai.Base.spidemo.impl;

import com.linsir.abc.pdai.Base.spidemo.HelloService;

/**
 * 英文实现的HelloService
 */
public class EnglishHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("Hello! This is English implementation of HelloService");
    }

    @Override
    public String getServiceName() {
        return "EnglishHelloService";
    }
}
