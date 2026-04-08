package com.linsir.system.modules.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.service.RolePermissionService;
import com.linsir.system.modules.rbac.service.RoleService;
import com.linsir.system.core.result.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.linsir.system.core.result.CommonResult.success;

/**
 * 角色 Controller
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    /**
     * 分页查询角色列表（支持查询条件）
     */
    @GetMapping("/page")
    public CommonResult<Page<Role>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String roleName,
                                         @RequestParam(required = false) String roleCode,
                                         @RequestParam(required = false) Integer status) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleName != null && !roleName.isEmpty(), Role::getRoleName, roleName)
               .like(roleCode != null && !roleCode.isEmpty(), Role::getRoleCode, roleCode)
               .eq(status != null, Role::getStatus, status)
               .orderByAsc(Role::getRoleSort);
        return success(roleService.page(page, wrapper));
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public CommonResult<Role> getById(@PathVariable Long id) {
        return success(roleService.getById(id));
    }

    /**
     * 根据角色编码查询角色
     */
    @GetMapping("/code/{roleCode}")
    public CommonResult<Role> getByRoleCode(@PathVariable String roleCode) {
        return success(roleService.getByRoleCode(roleCode));
    }

    /**
     * 新增角色
     */
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestBody Role role) {
        return success(roleService.save(role));
    }

    /**
     * 修改角色
     */
    @PutMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Role role) {
        return success(roleService.updateById(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        return success(roleService.removeById(id));
    }

    /**
     * 获取全部角色列表（不分页）
     */
    @GetMapping("/list")
    public CommonResult<List<Role>> list() {
        return success(roleService.list());
    }

    /**
     * 根据角色ID查询权限ID列表
     */
    @GetMapping("/{roleId}/permissions")
    public CommonResult<List<Long>> listPermissionIdsByRoleId(@PathVariable Long roleId) {
        return success(rolePermissionService.listPermissionIdsByRoleId(roleId));
    }

    /**
     * 分配角色权限
     */
    @PostMapping("/{roleId}/permissions")
    public CommonResult<Boolean> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return success(rolePermissionService.assignPermissions(roleId, permissionIds));
    }
}
