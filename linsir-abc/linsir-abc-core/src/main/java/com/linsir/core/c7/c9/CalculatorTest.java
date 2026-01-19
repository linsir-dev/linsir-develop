package com.linsir.core.c7.c9;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
    @BeforeEach
    public void init(){
        System.out.println("方法执行开始了。。。");
    }
    @AfterEach
    public void close(){
        System.out.println("方法执行结束了。。。");
    }
    @Test
    public void testAdd(){
        System.out.println("测试add方法");
        Calculator cal = new Calculator();
        int result = cal.add(20, 30);
        System.out.println(result);
        Assert.assertEquals(40,result);
    }
    @Test
    public void testSub(){
        System.out.println("测试sub方法");
        Calculator cal = new Calculator();
        int result = cal.sub(10, 30);
        System.out.println(result);
    }
}
