package com.linsir.subject.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.Duration;

/**
 * @ClassName : SpringMVCConfig
 * @Description : 相当于spring-servlet.xml
 * @Author : Linsir
 * @Date: 2023-12-02 22:21
 */

@Configuration
@ComponentScan("com.linsir.subject.sms")
@EnableWebMvc
@EnableWebSecurity
public class WebMvcSecurityConfig implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    /*@Bean
    public InternalResourceViewResolver setResourceViewResolver()
    {
            InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
            internalResourceViewResolver.setViewClass(JstlView.class);
            return internalResourceViewResolver;
    }*/
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
       /* registry.viewResolver(setResourceViewResolver());*/
        registry.jsp("/WEB-INF/jsp/",".jsp").viewClass(JstlView.class);
        registry.enableContentNegotiation(new MappingJackson2JsonView());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*registry.addResourceHandler("/statics/**")
                .addResourceLocations("/public", "/WEB-INF/statics/**")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));*/
        registry.addResourceHandler("/statics/**").addResourceLocations("/WEB-INF/statics/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login","/statics/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((authorize) ->{
                    authorize.loginPage("/login");
                });
        return http.build();
    }

}
