package com.linsir.test;

import com.linsir.service.OperationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTemplateServiceImplTest {

    @Qualifier("redisTemplateServiceImpl")
    @Autowired
    private OperationService operationService;

    @Test
    public void set()
    {
        operationService.set("s","ss", 10, TimeUnit.SECONDS);
    }


    @Test
    public void setString() {
        operationService.setString("str","str");
        operationService.setString("str","str1",4);

    }

    @Test
    public void getString() {
        operationService.setString("str","str");
        Assertions.assertEquals("str",operationService.getString("str"));
    }

    @Test
    public void getAndSetString() {
        //operationService.getAndSet("str","str");
        Assertions.assertEquals("str",operationService.getAndSet("str","str2"));
    }

    @Test
    public void appendString() {
        operationService.appendString("str","str2");
    }

    @Test
    public void getRedisStrSize()
    {
        Assertions.assertEquals(8L,operationService.getRedisStrSize("str"));
    }

   /* @Test
    public void leftPush()
    {

        Assertions.assertEquals(1L,operationService.leftPush("strList","Str1"));
        Assertions.assertEquals(2L,operationService.leftPush("strList","Str2"));
    }*/

    @Test
    public void size()
    {
        long size = operationService.Listsize("strList");
        Assertions.assertEquals(2,size);
    }

}
