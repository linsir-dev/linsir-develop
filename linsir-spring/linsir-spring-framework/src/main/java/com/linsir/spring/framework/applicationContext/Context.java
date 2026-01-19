package com.linsir.spring.framework.applicationContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @ClassName : Context
 * @Description : 描述集中applicationContext
 * @Author : Linsir
 * @Date: 2023-12-02 22:55
 */

public class Context {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        ApplicationContext ctx =
                new FileSystemXmlApplicationContext("conf/appContext.xml");
        /*ApplicationContext ctx = new ClassPathXmlApplicationContext(
                new String[] {"services.xml", "repositories.xml"}, MessengerService.class);*/

       /* ApplicationContext applicationContext1 = new  WebApplicationContext();*/
    }
}
