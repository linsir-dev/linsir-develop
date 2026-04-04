package com.linsir.security.controller;

import com.linsir.security.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录测试控制器
 * 用于测试基于用户名密码的认证
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthTestController {

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public UserResponse getCurrentUser() {
        UserResponse response = new UserResponse();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
            || "anonymousUser".equals(authentication.getPrincipal())) {
            response.setAuthenticated(false);
            return response;
        }
        
        Object principal = authentication.getPrincipal();
        
        response.setAuthenticated(true);
        response.setUsername(authentication.getName());
        response.setPrincipal(principal.toString());
        
        if (principal instanceof UserDetails) {
            response.setRoles(((UserDetails) principal).getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList());
        }
        
        response.setAuthenticationClass(authentication.getClass().getSimpleName());
        
        return response;
    }

    /**
     * 受保护的接口（需要认证）
     */
    @GetMapping("/protected")
    public Map<String, Object> protectedResource() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "您已访问受保护的资源");
        result.put("data", "这是受保护的数据内容");
        return result;
    }
}
