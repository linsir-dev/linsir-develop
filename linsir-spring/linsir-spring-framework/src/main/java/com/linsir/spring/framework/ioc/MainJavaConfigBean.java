package com.linsir.spring.framework.ioc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/1 15:02
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
public class MainJavaConfigBean {
    public static  void main(String[] args)
    {
        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext(MainJavaConfigBean.class);
        applicationContext.close();
    }
}
