package com.linsir.spring.framework.service.impl;


import com.linsir.spring.framework.service.MyService;
import org.springframework.stereotype.Service;

/**
 * @ClassName : MyService
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 14:08
 */

@Service
public class MyServiceImpl implements MyService {
    @Override
    public void doService() {
        System.out.println("xxxx");
    }
}
