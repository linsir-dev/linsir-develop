package com.linsir.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置类
 * 
 * 配置 Spring MVC 的相关配置，包括：
 * - 视图控制器（View Controllers）
 * - 静态资源映射（Resource Handlers）
 * - 跨域配置（CORS）
 * - 拦截器配置（Interceptors）
 * 
 * 注意：引入 Thymeleaf 后，部分配置由 Thymeleaf 自动处理
 * - 模板文件自动从 classpath:/templates/ 目录加载
 * - 自动添加 .html 后缀
 * - 静态资源默认从 classpath:/static/ 目录加载
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 配置视图控制器
     * 
     * 将请求路径直接映射到视图，不需要 Controller 处理
     * 适用于简单的静态页面
     * 
     * 注意：如果已经在 Controller 中定义了映射，这里不应该重复配置
     * 否则会导致冲突或循环跳转
     * 
     * @param registry ViewControllerRegistry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 登录页面（如果没有 LoginController，可以取消注释）
        // registry.addViewController("/login").setViewName("login");
        
        // 错误页面（如果没有 ErrorController，可以取消注释）
        // registry.addViewController("/error").setViewName("error");
        
        // 其他常见页面映射
        // registry.addViewController("/about").setViewName("about");
        // registry.addViewController("/dashboard").setViewName("dashboard");
    }

    /**
     * 配置静态资源映射
     * 
     * 注意：Spring Boot 默认已经配置了静态资源映射
     * - /static/** → classpath:/static/
     * - /public/** → classpath:/public/
     * - /resources/** → classpath:/resources/
     * 
     * 如果不需要自定义静态资源路径，可以不配置此项
     * 
     * @param registry ResourceHandlerRegistry
     */
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     // 映射 /static/** 到 classpath:/static/ 目录
    //     registry.addResourceHandler("/static/**")
    //             .addResourceLocations("classpath:/static/");
        
    //     // 映射 /css/** 到 classpath:/static/css/
    //     registry.addResourceHandler("/css/**")
    //             .addResourceLocations("classpath:/static/css/");
        
    //     // 映射 /js/** 到 classpath:/static/js/
    //     registry.addResourceHandler("/js/**")
    //             .addResourceLocations("classpath:/static/js/");
        
    //     // 映射 /images/** 到 classpath:/static/images/
    //     registry.addResourceHandler("/images/**")
    //             .addResourceLocations("classpath:/static/images/");
    // }

    /**
     * 配置跨域访问（CORS）
     * 
     * 如果前后端分离，前端和后端不在同一域名，需要配置 CORS
     * 
     * @param registry CorsRegistry
     */
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/api/**")
    //             .allowedOrigins("http://localhost:3000", "https://your-frontend.com")
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //             .allowedHeaders("*")
    //             .allowCredentials(true)
    //             .maxAge(3600);
    // }

    /**
     * 配置拦截器
     * 
     * 添加自定义的 HandlerInterceptor
     * 
     * @param registry InterceptorRegistry
     */
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     registry.addInterceptor(new CustomInterceptor())
    //             .addPathPatterns("/api/**")
    //             .excludePathPatterns("/api/auth/**");
    // }

    /**
     * 配置路径匹配
     * 
     * 配置 URL 路径的匹配规则
     * 
     * @param configurer PathMatchConfigurer
     */
    // @Override
    // public void configurePathMatch(PathMatchConfigurer configurer) {
    //     // 配置是否使用后缀模式匹配
    //     configurer.setUseSuffixPatternMatch(false);
    //     // 配置是否使用注册的后缀
    //     configurer.setUseRegisteredSuffixPatternMatch(true);
    //     // 配置是否使用斜杠匹配
    //     configurer.setUseTrailingSlashMatch(true);
    // }

    /**
     * 配置内容协商
     * 
     * 根据请求参数或 Header 决定返回的内容类型
     * 
     * @param configurer ContentNegotiationConfigurer
     */
    // @Override
    // public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    //     // 基于请求参数
    //     configurer.favorParameter(true)
    //             .parameterName("mediaType")
    //             .ignoreAcceptHeader(false)
    //             .useJaf(false)
    //             .defaultContentType(MediaType.APPLICATION_JSON)
    //             .mediaType("json", MediaType.APPLICATION_JSON)
    //             .mediaType("xml", MediaType.APPLICATION_XML);
    // }
}
