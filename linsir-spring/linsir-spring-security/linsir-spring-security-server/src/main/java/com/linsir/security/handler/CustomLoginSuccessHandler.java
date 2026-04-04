package com.linsir.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义登录成功处理器
 * 返回 JSON 格式响应
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 登录成功时的处理逻辑
     * 
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication 认证信息
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(
            "{\"code\":200,\"message\":\"登录成功\",\"data\":{\"username\":\"" + 
            authentication.getName() + "\"}}"
        );
    }
}
