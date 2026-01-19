package com.linsir.spring.framework.ioc;

import com.linsir.spring.framework.ioc.dependencies.CommandManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/8/24 13:20
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
public class MainXml {

    public static void main(String[] args)
    {

        ApplicationContext sc= new ClassPathXmlApplicationContext("Beans.xml");

        String[] definitionNames = sc.getBeanDefinitionNames();

        String[] aliases = sc.getAliases("person");

       Arrays.stream(definitionNames).forEach(System.out::println);

       Arrays.stream(aliases).forEach(System.out::println);


       /*--------------------------------------------------------------------------------------*/

       CommandManager commandManager = sc.getBean(CommandManager.class);

       System.out.println(commandManager.process("代号：1527"));

    }
}
