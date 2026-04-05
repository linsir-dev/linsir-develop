package com.linsir.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.security.entity.Role;
import com.linsir.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理 Controller
 * 使用 MyBatis Plus 提供的通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表（支持搜索）
     * 使用 QueryWrapper 直接在 Controller 中构建查询条件
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "rows", defaultValue = "10") int rows,
            @RequestParam(name = "roleCode", required = false) String roleCode,
            @RequestParam(name = "roleName", required = false) String roleName,
            @RequestParam(name = "status", required = false) Integer status) {

        // 创建分页参数
        Page<Role> pageParam = new Page<>(page, rows);

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        // 添加搜索条件
        if (StringUtils.hasText(roleCode)) {
            wrapper.like(Role::getRoleCode, roleCode);
        }
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(Role::getCreateTime);

        // 执行分页查询
        IPage<Role> rolePage = roleService.page(pageParam, wrapper);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", rolePage.getTotal());
        result.put("rows", rolePage.getRecords());

        return ResponseEntity.ok(result);
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);

        Map<String, Object> result = new HashMap<>();
        if (role != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", role);
        } else {
            result.put("code", 404);
            result.put("message", "角色不存在");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 创建角色
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Role role) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = roleService.save(role);
            if (success) {
                result.put("code", 200);
                result.put("message", "创建成功");
                result.put("data", role);
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
     * 更新角色
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("id") Long id,
            @RequestBody Role role) {
        role.setId(id);

        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = roleService.updateById(role);
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
     * 删除角色
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();

        boolean success = roleService.removeById(id);
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
     * 批量删除角色
     */
    @DeleteMapping("/delete/batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();

        boolean success = roleService.removeByIds(ids);
        if (success) {
            result.put("code", 200);
            result.put("message", "批量删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "批量删除失败");
        }

        return ResponseEntity.ok(result);
    }
}
