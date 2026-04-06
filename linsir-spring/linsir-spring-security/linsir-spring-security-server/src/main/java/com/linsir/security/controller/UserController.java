package com.linsir.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.security.entity.Role;
import com.linsir.security.entity.User;
import com.linsir.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Controller
 * 使用 MyBatis Plus 提供的通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表（支持搜索）
     * 使用 QueryWrapper 直接在 Controller 中构建查询条件
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "rows", defaultValue = "10") int rows,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "status", required = false) Integer status) {

        // 创建分页参数
        Page<User> pageParam = new Page<>(page, rows);

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 添加搜索条件
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(User::getCreateTime);
        
        // 执行分页查询
        IPage<User> userPage = userService.page(pageParam, wrapper);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", userPage.getTotal());
        result.put("rows", userPage.getRecords());

        return ResponseEntity.ok(result);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = userService.save(user);
            if (success) {
                result.put("code", 200);
                result.put("message", "创建成功");
                result.put("data", user);
            } else {
                result.put("code", 500);
                result.put("message", "创建失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 更新用户
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("id") Long id,
            @RequestBody User user) {
        user.setId(id);

        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = userService.updateById(user);
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
            } else {
                result.put("code", 500);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();

        boolean success = userService.removeById(id);
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "删除失败");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/delete/batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();

        boolean success = userService.removeByIds(ids);
        if (success) {
            result.put("code", 200);
            result.put("message", "批量删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "批量删除失败");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 查询用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.ok(result);
        }

        // 调用 Service 获取用户角色列表
        List<Role> roles = userService.getUserRoles(userId);

        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", roles);

        return ResponseEntity.ok(result);
    }

    /**
     * 给用户分配角色
     */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<Map<String, Object>> assignRoles(
            @PathVariable("userId") Long userId,
            @RequestBody List<Long> roleIds) {
        Map<String, Object> result = new HashMap<>();

        // 查询用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return ResponseEntity.ok(result);
        }

        try {
            // 调用 Service 分配角色
            userService.assignRoles(userId, roleIds);

            result.put("code", 200);
            result.put("message", "角色分配成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "角色分配失败: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}
