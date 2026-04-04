package com.linsir.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * SecurityContextHolder 测试控制器
 * 用于演示 SecurityContextHolder 的基本操作
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/security-context")
public class SecurityContextTestController {

    /**
     * 1. 手动创建认证对象并设置到 SecurityContextHolder
     * 演示如何程序化地创建认证信息
     * 
     * 注意：在 Web 应用中，SecurityContextPersistenceFilter 会自动创建 SecurityContext
     * 我们只需要获取现有的上下文并设置认证信息即可
     */
    @PostMapping("/create-authentication")
    public Map<String, Object> createAuthentication(@RequestBody Map<String, String> request,
                                                     HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        
        String username = request.getOrDefault("username", "testuser");
        String role = request.getOrDefault("role", "ROLE_USER");
        
        // 1. 获取当前安全上下文（由 SecurityContextPersistenceFilter 自动创建）
        SecurityContext context = SecurityContextHolder.getContext();
        
        // 2. 创建 UserDetails 对象（主体）
        UserDetails userDetails = User.builder()
                .username(username)
                .password("{noop}password")  // {noop} 表示不加密
                .roles(role.replace("ROLE_", ""))  // 去掉 ROLE_ 前缀
                .build();
        
        // 3. 创建认证对象
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,           // 主体（用户信息）
                null,                  // 凭证（已认证后不需要）
                userDetails.getAuthorities()  // 权限
        );
        
        // 4. 设置认证信息到上下文
        context.setAuthentication(authentication);
        
        // 5. 手动保存到 Session（确保跨请求保持）
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        
        // 验证设置是否成功（在同一个请求中验证）
        SecurityContext verifyContext = SecurityContextHolder.getContext();
        Authentication verifyAuth = verifyContext.getAuthentication();
        
        // 返回结果
        result.put("success", true);
        result.put("message", "认证对象创建成功");
        result.put("username", username);
        result.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        result.put("verifiedInSameRequest", verifyAuth != null && verifyAuth.isAuthenticated() 
                && verifyAuth.getName().equals(username));
        result.put("sessionId", session.getId());
        
        return result;
    }

    /**
     * 2. 获取当前认证信息
     * 演示如何从 SecurityContextHolder 获取当前用户
     */
    @GetMapping("/get-current-user")
    public Map<String, Object> getCurrentUser() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取 SecurityContext
        SecurityContext context = SecurityContextHolder.getContext();
        
        // 获取 Authentication
        Authentication authentication = context.getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            result.put("authenticated", false);
            result.put("message", "当前没有认证信息");
            return result;
        }
        
        // 获取用户名
        String username = authentication.getName();
        
        // 获取主体
        Object principal = authentication.getPrincipal();
        
        // 获取权限集合
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        result.put("authenticated", true);
        result.put("username", username);
        result.put("principal", principal.toString());
        result.put("authorities", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        result.put("authenticationClass", authentication.getClass().getSimpleName());
        
        return result;
    }

    /**
     * 3. 清除当前认证信息
     * 演示如何清理 SecurityContextHolder
     */
    @PostMapping("/clear-context")
    public Map<String, Object> clearContext(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 清除当前线程的安全上下文
        SecurityContextHolder.clearContext();
        
        // 2. 清除 Session 中的 SecurityContext（确保下次请求不会恢复）
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("SPRING_SECURITY_CONTEXT");
        }
        
        result.put("success", true);
        result.put("message", "安全上下文已清除");
        
        return result;
    }

    /**
     * 4. 检查认证状态
     * 演示认证状态的检查
     */
    @GetMapping("/check-status")
    public Map<String, Object> checkStatus() {
        Map<String, Object> result = new HashMap<>();
        
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
        result.put("hasContext", context != null);
        result.put("hasAuthentication", authentication != null);
        
        if (authentication != null) {
            result.put("isAuthenticated", authentication.isAuthenticated());
            result.put("username", authentication.getName());
            result.put("principalType", authentication.getPrincipal().getClass().getSimpleName());
        }
        
        return result;
    }

    /**
     * 5. 创建带多个权限的认证对象
     * 演示如何设置多个角色
     */
    @PostMapping("/create-with-roles")
    public Map<String, Object> createWithRoles(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        String username = (String) request.getOrDefault("username", "admin");
        
        // 获取角色列表
        @SuppressWarnings("unchecked")
        java.util.List<String> roles = (java.util.List<String>) request.getOrDefault("roles", 
                java.util.List.of("ROLE_USER", "ROLE_ADMIN"));
        
        // 1. 获取当前安全上下文（由 SecurityContextPersistenceFilter 自动创建）
        SecurityContext context = SecurityContextHolder.getContext();
        
        // 2. 创建 UserDetails
        UserDetails userDetails = new User(username, "{noop}password", 
                AuthorityUtils.createAuthorityList(roles.toArray(new String[0])));
        
        // 3. 创建认证对象
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        
        // 4. 设置到上下文（SecurityContext 已由 SecurityContextPersistenceFilter 创建）
        context.setAuthentication(authentication);
        
        result.put("success", true);
        result.put("username", username);
        result.put("roles", roles);
        result.put("totalAuthorities", authentication.getAuthorities().size());
        
        return result;
    }

    /**
     * 6. 需要认证才能访问的接口
     * 使用 @PreAuthorize 进行权限控制
     */
    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> protectedResource() {
        Map<String, Object> result = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        result.put("message", "这是受保护的资源");
        result.put("currentUser", authentication.getName());
        result.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        
        return result;
    }

    /**
     * 7. 需要特定角色才能访问的接口
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminOnly() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("message", "只有 ADMIN 角色可以访问");
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 8. 完整流程演示：创建 -> 验证 -> 清除
     */
    @PostMapping("/full-demo")
    public Map<String, Object> fullDemo() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> steps = new HashMap<>();
        
        // 步骤1：检查初始状态
        SecurityContext contextBefore = SecurityContextHolder.getContext();
        Authentication authBefore = contextBefore.getAuthentication();
        steps.put("step1_initial", Map.of(
                "hasAuthentication", authBefore != null && authBefore.isAuthenticated(),
                "username", authBefore != null ? authBefore.getName() : "anonymous"
        ));
        
        // 步骤2：创建认证对象
        // 获取当前安全上下文（由 SecurityContextPersistenceFilter 自动创建）
        SecurityContext context = SecurityContextHolder.getContext();
        UserDetails userDetails = User.builder()
                .username("demoUser")
                .password("{noop}password")
                .roles("USER", "ADMIN")
                .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);
        
        steps.put("step2_created", Map.of(
                "username", "demoUser",
                "roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        ));
        
        // 步骤3：验证认证信息
        SecurityContext contextAfter = SecurityContextHolder.getContext();
        Authentication authAfter = contextAfter.getAuthentication();
        steps.put("step3_verified", Map.of(
                "isAuthenticated", authAfter.isAuthenticated(),
                "username", authAfter.getName(),
                "principalType", authAfter.getPrincipal().getClass().getSimpleName()
        ));
        
        // 步骤4：清除上下文
        SecurityContextHolder.clearContext();
        SecurityContext contextCleared = SecurityContextHolder.getContext();
        Authentication authCleared = contextCleared.getAuthentication();
        steps.put("step4_cleared", Map.of(
                "hasAuthentication", authCleared != null && authCleared.isAuthenticated(),
                "isAnonymous", authCleared == null || 
                        authCleared.getPrincipal().equals("anonymousUser")
        ));
        
        result.put("success", true);
        result.put("steps", steps);
        result.put("summary", "完整演示：创建 -> 验证 -> 清除");
        
        return result;
    }
}
