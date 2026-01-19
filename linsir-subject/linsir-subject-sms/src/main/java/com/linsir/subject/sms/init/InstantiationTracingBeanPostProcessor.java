package com.linsir.subject.sms.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @ClassName : InstantiationTracingBeanPostProcessor
 * @Description : 描述bean创造的生命周期
 * 展示了一个自定义的 BeanPostProcessor 实现，它在容器创建每个Bean时调用 toString() 方法，并将结果字符串打印到系统控制台。
 * @Author : Linsir
 * @Date: 2023-12-04 13:10
 */

@Component
public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //log.info(String.format("创建：S%",beanName));

        //log.info("BeanName:{0}开始创建",beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Bean------------ '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}
