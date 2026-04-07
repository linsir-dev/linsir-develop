package com.linsir.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义认证入口点处理器
 *
 * 处理未认证访问（401 Unauthorized）
 * 返回 JSON 格式的错误信息
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 设置响应状态码为 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");

        // 构建错误响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", "未认证，请先登录");
        result.put("path", request.getRequestURI());
        result.put("timestamp", System.currentTimeMillis());

        // 转换为 JSON 并写入响应
        PrintWriter out = response.getWriter();
        out.write("{\"code\":" + result.get("code") + ",\"message\":\"" + result.get("message") + "\",\"path\":\"" + result.get("path") + "\",\"timestamp\":" + result.get("timestamp") + "}");
    }
}
