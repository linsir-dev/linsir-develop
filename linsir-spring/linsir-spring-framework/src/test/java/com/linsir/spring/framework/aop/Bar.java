package com.linsir.spring.framework.aop;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName : Bar
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 23:16
 */

public class Bar implements IBar{

    @Autowired
    private Dep dep;

    @Override
    public String perform(String message) {
        System.out.println("run bar " + message);
        return dep.perform("aspect");

    }
}
