package com.linsir.test;


import com.linsir.service.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class OperationServiceTest {

    @Qualifier("operationServiceImpl")
    @Autowired
    private OperationService operationService;


    @Test
    public void set()
    {
        operationService.set("p","ss", 10, TimeUnit.SECONDS);
    }

    @Test
    public void setStringTest() {
        operationService.setString("yx", "def");
    }


}
