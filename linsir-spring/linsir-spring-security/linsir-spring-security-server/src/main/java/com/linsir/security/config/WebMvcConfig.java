package com.linsir.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置静态资源映射
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加静态资源处理器
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 配置 CSS 资源映射
        registry.addResourceHandler("/static/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        // 配置 JavaScript 资源映射
        registry.addResourceHandler("/static/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        // 配置 EasyUI 资源映射
        registry.addResourceHandler("/static/easyui/**")
                .addResourceLocations("classpath:/static/easyui/");
        
        // 配置图片资源映射
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
