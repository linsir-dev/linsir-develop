package com.linsir.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linsir.security.entity.Permission;
import com.linsir.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理 Controller
 * 使用 MyBatis Plus 提供的通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询权限列表（树形结构）
     * 返回所有权限数据，前端使用 TreeGrid 展示
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "permissionName", required = false) String permissionName,
            @RequestParam(name = "status", required = false) Integer status) {

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();

        // 添加搜索条件
        if (StringUtils.hasText(permissionName)) {
            wrapper.like(Permission::getPermissionName, permissionName);
        }
        if (status != null) {
            wrapper.eq(Permission::getStatus, status);
        }

        // 按排序号升序、创建时间降序排序
        wrapper.orderByAsc(Permission::getSortOrder)
               .orderByDesc(Permission::getCreateTime);

        // 执行查询
        List<Permission> permissionList = permissionService.list(wrapper);

        // 构建树形结构
        List<Map<String, Object>> treeData = buildTreeData(permissionList);

        // 封装返回结果 - EasyUI TreeGrid 需要 {total: xx, rows: []} 格式
        Map<String, Object> result = new HashMap<>();
        result.put("total", treeData.size());
        result.put("rows", treeData);

        return ResponseEntity.ok(result);
    }

    /**
     * 构建树形结构数据
     * EasyUI TreeGrid 需要扁平化数据，通过 _parentId 字段标识父子关系
     * @param permissionList 权限列表
     * @return 扁平化的树形数据（带有 _parentId 字段）
     */
    private List<Map<String, Object>> buildTreeData(List<Permission> permissionList) {
        // 先按 parentId 分组，用于判断是否有子节点
        Map<Long, List<Permission>> childrenMap = permissionList.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));

        // 转换为Map列表
        return permissionList.stream().map(permission -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", permission.getId());
            map.put("parentId", permission.getParentId());
            map.put("permissionCode", permission.getPermissionCode());
            map.put("permissionName", permission.getPermissionName());
            map.put("resourceType", permission.getResourceType());
            map.put("url", permission.getUrl());
            map.put("method", permission.getMethod());
            map.put("icon", permission.getIcon());
            map.put("sortOrder", permission.getSortOrder());
            map.put("status", permission.getStatus());
            map.put("createTime", permission.getCreateTime());
            map.put("updateTime", permission.getUpdateTime());

            // TreeGrid 需要的 _parentId 字段（用于标识父节点）
            map.put("_parentId", permission.getParentId() == 0 ? null : permission.getParentId());

            // 判断是否有子节点，设置展开状态
            List<Permission> children = childrenMap.get(permission.getId());
            if (children != null && !children.isEmpty()) {
                map.put("state", "closed"); // 有子节点，可展开
            } else {
                map.put("state", "open"); // 无子节点，叶子节点
            }

            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 查询权限详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Long id) {
        Permission permission = permissionService.getById(id);

        Map<String, Object> result = new HashMap<>();
        if (permission != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", permission);
        } else {
            result.put("code", 404);
            result.put("message", "权限不存在");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 创建权限
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Permission permission) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 如果父ID为空，设置为0（顶级）
            if (permission.getParentId() == null) {
                permission.setParentId(0L);
            }

            boolean success = permissionService.save(permission);
            if (success) {
                result.put("code", 200);
                result.put("message", "创建成功");
                result.put("data", permission);
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
     * 更新权限
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("id") Long id,
            @RequestBody Permission permission) {
        permission.setId(id);

        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = permissionService.updateById(permission);
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
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();

        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        long childCount = permissionService.count(wrapper);

        if (childCount > 0) {
            result.put("code", 500);
            result.put("message", "该权限下存在子权限，请先删除子权限");
            return ResponseEntity.ok(result);
        }

        boolean success = permissionService.removeById(id);
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
     * 查询所有权限（用于下拉选择）
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAll() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, 1)
               .orderByAsc(Permission::getSortOrder);

        List<Permission> permissionList = permissionService.list(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", permissionList);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有权限（树形结构，用于角色赋权）
     * 返回嵌套的树形结构，包含所有类型的权限
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getPermissionTree() {
        // 查询所有权限（状态为启用）
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, 1)
               .orderByAsc(Permission::getSortOrder);

        List<Permission> permissionList = permissionService.list(wrapper);

        // 构建权限树形结构
        List<Map<String, Object>> permissionTree = buildPermissionTreeForRole(permissionList);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", permissionTree);

        return ResponseEntity.ok(result);
    }

    /**
     * 构建权限树形结构（用于角色赋权）
     * @param permissionList 权限列表
     * @return 嵌套的树形结构数据
     */
    private List<Map<String, Object>> buildPermissionTreeForRole(List<Permission> permissionList) {
        // 转换为Map列表
        List<Map<String, Object>> mapList = permissionList.stream().map(permission -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", permission.getId());
            map.put("parentId", permission.getParentId());
            map.put("permissionCode", permission.getPermissionCode());
            map.put("permissionName", permission.getPermissionName());
            map.put("resourceType", permission.getResourceType());
            map.put("url", permission.getUrl());
            map.put("icon", permission.getIcon());
            map.put("sortOrder", permission.getSortOrder());
            return map;
        }).collect(Collectors.toList());

        // 构建嵌套树形结构
        return buildTreeRecursive(mapList, 0L);
    }

    /**
     * 查询可选的父权限列表（用于下拉选择）
     * 返回所有菜单和按钮类型的权限（可作为父权限）
     */
    @GetMapping("/parent-options")
    public ResponseEntity<?> getParentOptions() {
        // 查询所有可作为父权限的权限（菜单和按钮类型）
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Permission::getResourceType, Arrays.asList("menu", "button"))
               .eq(Permission::getStatus, 1)
               .orderByAsc(Permission::getSortOrder);

        List<Permission> permissionList = permissionService.list(wrapper);

        // 添加顶级选项
        List<Map<String, Object>> options = new ArrayList<>();

        // 添加"无（顶级）"选项
        Map<String, Object> topOption = new HashMap<>();
        topOption.put("id", 0);
        topOption.put("permissionName", "无（顶级权限）");
        options.add(topOption);

        // 添加其他权限选项
        for (Permission permission : permissionList) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", permission.getId());
            option.put("permissionName", permission.getPermissionName());
            options.add(option);
        }

        // 直接返回数据数组（EasyUI Combobox 期望的格式）
        return ResponseEntity.ok(options);
    }

    /**
     * 查询菜单列表（树形结构，用于前端菜单展示）
     * 只返回 resourceType 为 menu 且状态为启用的权限
     */
    @GetMapping("/menu/list")
    public ResponseEntity<Map<String, Object>> getMenuList() {
        // 查询所有菜单类型的权限（状态为启用）
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getResourceType, "menu")
               .eq(Permission::getStatus, 1)
               .orderByAsc(Permission::getSortOrder);

        List<Permission> menuList = permissionService.list(wrapper);

        // 构建菜单树形结构
        List<Map<String, Object>> menuTree = buildMenuTree(menuList);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", menuTree);

        return ResponseEntity.ok(result);
    }

    /**
     * 构建菜单树形结构
     * @param menuList 菜单列表
     * @return 嵌套的树形结构数据
     */
    private List<Map<String, Object>> buildMenuTree(List<Permission> menuList) {
        // 转换为Map列表
        List<Map<String, Object>> mapList = menuList.stream().map(menu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", menu.getId());
            map.put("parentId", menu.getParentId());
            map.put("permissionCode", menu.getPermissionCode());
            map.put("permissionName", menu.getPermissionName());
            map.put("url", menu.getUrl());
            map.put("icon", menu.getIcon());
            map.put("sortOrder", menu.getSortOrder());
            return map;
        }).collect(Collectors.toList());

        // 构建嵌套树形结构
        return buildTreeRecursive(mapList, 0L);
    }

    /**
     * 递归构建树形结构
     * @param mapList 所有菜单Map列表
     * @param parentId 父ID
     * @return 嵌套的树形结构列表
     */
    private List<Map<String, Object>> buildTreeRecursive(List<Map<String, Object>> mapList, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            Long currentParentId = (Long) map.get("parentId");
            if (currentParentId != null && currentParentId.equals(parentId)) {
                // 递归获取子节点
                List<Map<String, Object>> children = buildTreeRecursive(mapList, (Long) map.get("id"));
                if (!children.isEmpty()) {
                    map.put("children", children);
                }
                result.add(map);
            }
        }

        return result;
    }
}
