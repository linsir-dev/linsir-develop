package com.linsir.spring.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.Duration;

/**
 * @ClassName : WebConfig
 * @Description : Mvc配置
 * @Author : Linsir
 * @Date: 2023-12-09 01:46
 */

@Configuration
@ComponentScan("com.linsir.spring.springmvc")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        /* registry.viewResolver(setResourceViewResolver());*/
        registry.jsp("/WEB-INF/jsp/",".jsp").viewClass(JstlView.class);
        registry.enableContentNegotiation(new MappingJackson2JsonView());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
