package com.linsir.subject.sms.init;

;
import com.linsir.subject.sms.config.ApplicationConfig;
import com.linsir.subject.sms.config.DataConfig;
import com.linsir.subject.sms.config.WebMvcSecurityConfig;
import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @ClassName : WebInitializer
 * @Description : 相当于web.xml
 * @Author : Linsir
 * @Date: 2023-12-02 22:20
 */
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {WebMvcSecurityConfig.class ,DataConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
                new HiddenHttpMethodFilter(),
                new CharacterEncodingFilter(),
                new DelegatingFilterProxy("springSecurityFilterChain")};
    }
}


