package com.linsir.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义登录失败处理器
 * 返回 JSON 格式响应
 * 根据不同的异常类型返回不同的错误提示
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    /**
     * 登录失败时的处理逻辑
     * 
     * @param request 请求对象
     * @param response 响应对象
     * @param exception 认证异常
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        // 根据异常类型返回友好的错误信息
        String message;
        if (exception instanceof BadCredentialsException) {
            // 用户名或密码错误（包括用户不存在的情况）
            message = "用户名或密码错误";
        } else if (exception instanceof LockedException) {
            // 账户被锁定
            message = "账户已被锁定，请联系管理员";
        } else if (exception instanceof DisabledException) {
            // 账户被禁用
            message = "账户已被禁用，请联系管理员";
        } else if (exception instanceof AccountExpiredException) {
            // 账户已过期
            message = "账户已过期，请联系管理员";
        } else if (exception instanceof CredentialsExpiredException) {
            // 密码已过期
            message = "密码已过期，请修改密码后重新登录";
        } else {
            // 其他异常（不暴露具体错误信息，防止信息泄露）
            message = "用户名或密码错误";
        }
        
        response.getWriter().write(
            "{\"code\":401,\"message\":\"" + message + "\"}"
        );
    }
}
