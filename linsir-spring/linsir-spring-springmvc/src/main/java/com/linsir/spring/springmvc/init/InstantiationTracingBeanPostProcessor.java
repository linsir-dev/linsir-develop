package com.linsir.spring.springmvc.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @ClassName : InstantiationTracingBeanPostProcessor
 * @Description : Bean 生命周期
 * @Author : Linsir
 * @Date: 2023-12-09 01:48
 */

public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //log.info(String.format("创建：S%",beanName));
        System.out.println(beanName);
        //log.info("BeanName:{0}开始创建",beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}
