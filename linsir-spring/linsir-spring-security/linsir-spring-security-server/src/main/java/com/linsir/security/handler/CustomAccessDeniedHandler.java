package com.linsir.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义访问拒绝处理器
 *
 * 处理未授权访问（403 Forbidden）
 * 返回 JSON 格式的错误信息
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // 设置响应状态码为 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");

        // 构建错误响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", "访问被拒绝，您没有权限执行此操作");
        result.put("path", request.getRequestURI());
        result.put("timestamp", System.currentTimeMillis());

        // 转换为 JSON 并写入响应
        PrintWriter out = response.getWriter();
        out.write("{\"code\":" + result.get("code") + ",\"message\":\"" + result.get("message") + "\",\"path\":\"" + result.get("path") + "\",\"timestamp\":" + result.get("timestamp") + "}");
    }
}
