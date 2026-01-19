package com.linsir.spring.framework.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @ClassName : InstantiationTracingBeanPostProcessor
 * @Description : bean 跨展
 * @Author : Linsir
 * @Date: 2023-12-01 04:57
 */

public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}
