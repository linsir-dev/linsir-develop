package com.linsir.spring.springmvc.init;

import com.linsir.spring.springmvc.config.ApplicationConfig;
import com.linsir.spring.springmvc.config.WebConfig;
import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @ClassName : WebInitializer
 * @Description : 相当于web.xml 配置
 * @Author : Linsir
 * @Date: 2023-12-09 01:48
 */

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { ApplicationConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
                new HiddenHttpMethodFilter(), new CharacterEncodingFilter() };
    }
}
