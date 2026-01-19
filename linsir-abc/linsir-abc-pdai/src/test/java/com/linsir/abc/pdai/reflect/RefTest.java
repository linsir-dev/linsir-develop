package com.linsir.abc.pdai.reflect;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RefTest {

    private static Logger logger = LoggerFactory.getLogger(RefTest.class);
    @Test
    public void classTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {


        logger.info("根据类名:  \t" + User.class);
        logger.info("根据对象:  \t" + new User().getClass());
        logger.info("根据全限定类名:\t" + Class.forName("com.linsir.pdai.reflect.User"));

        Class<User> userClass = User.class;
        // 常用的方法
        logger.info("获取全限定类名:\t" + userClass.getName());
        logger.info("获取类名:\t" + userClass.getSimpleName());
        logger.info("实例化:\t" + userClass.newInstance());
    }
}
