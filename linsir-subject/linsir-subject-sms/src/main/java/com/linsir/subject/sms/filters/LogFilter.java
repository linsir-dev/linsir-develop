package com.linsir.subject.sms.filters;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * @ClassName : LogFilter
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-12 18:00
 */

public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("LOG----------"+servletRequest.getRemoteAddr());
    }
}
