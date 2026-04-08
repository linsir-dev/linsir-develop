package com.linsir.system.modules.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.service.PermissionService;
import com.linsir.system.core.result.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.linsir.system.core.result.CommonResult.success;

/**
 * 权限 Controller
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 分页查询权限列表（支持查询条件）
     */
    @GetMapping("/page")
    public CommonResult<Page<Permission>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) String permissionName,
                                               @RequestParam(required = false) String permissionCode,
                                               @RequestParam(required = false) Integer permissionType,
                                               @RequestParam(required = false) Integer status) {
        Page<Permission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(permissionName != null && !permissionName.isEmpty(), Permission::getPermissionName, permissionName)
               .like(permissionCode != null && !permissionCode.isEmpty(), Permission::getPermissionCode, permissionCode)
               .eq(permissionType != null, Permission::getPermissionType, permissionType)
               .eq(status != null, Permission::getStatus, status)
               .orderByAsc(Permission::getPermissionSort);
        return success(permissionService.page(page, wrapper));
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    public CommonResult<Permission> getById(@PathVariable Long id) {
        return success(permissionService.getById(id));
    }

    /**
     * 根据权限标识查询权限
     */
    @GetMapping("/code/{permissionCode}")
    public CommonResult<Permission> getByPermissionCode(@PathVariable String permissionCode) {
        return success(permissionService.getByPermissionCode(permissionCode));
    }

    /**
     * 根据父权限ID查询子权限列表
     */
    @GetMapping("/parent/{parentId}")
    public CommonResult<List<Permission>> listByParentId(@PathVariable Long parentId) {
        return success(permissionService.listByParentId(parentId));
    }

    /**
     * 新增权限
     */
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestBody Permission permission) {
        return success(permissionService.save(permission));
    }

    /**
     * 修改权限
     */
    @PutMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Permission permission) {
        return success(permissionService.updateById(permission));
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        return success(permissionService.removeById(id));
    }

    /**
     * 获取全部权限列表（不分页）
     */
    @GetMapping("/list")
    public CommonResult<List<Permission>> list() {
        return success(permissionService.list());
    }

    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/role/{roleId}")
    public CommonResult<List<Permission>> listByRoleId(@PathVariable Long roleId) {
        return success(permissionService.listByRoleId(roleId));
    }
}
