package com.linsir.spring.framework.web;

import com.linsir.spring.framework.config.AppTestConfig;
import com.linsir.spring.framework.config.WebAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @ClassName : WacTests
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 21:09
 */

@SpringJUnitConfig(name="ContextConfiguration",
        classes = {WebAppConfig.class})
public class WacTests {

    @Autowired
    WebApplicationContext wac; // cached

    @Autowired
    MockServletContext servletContext; // cached

    @Autowired
    MockHttpSession session;

    @Autowired
    MockHttpServletRequest request;

    @Autowired
    MockHttpServletResponse response;

    @Autowired
    ServletWebRequest webRequest;
}
