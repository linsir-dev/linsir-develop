package com.linsir.system.modules.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.entity.User;
import com.linsir.system.modules.rbac.service.UserRoleService;
import com.linsir.system.modules.rbac.service.UserService;
import com.linsir.system.core.result.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.linsir.system.core.result.CommonResult.success;

/**
 * 用户 Controller
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表（支持查询条件）
     */
    @GetMapping("/page")
    public CommonResult<Page<User>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) String nickname,
                                          @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), User::getUsername, username)
               .like(nickname != null && !nickname.isEmpty(), User::getNickname, nickname)
               .eq(status != null, User::getStatus, status)
               .orderByDesc(User::getCreateTime);
        return success(userService.page(page, wrapper));
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public CommonResult<User> getById(@PathVariable Long id) {
        return success(userService.getById(id));
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public CommonResult<User> getByUsername(@PathVariable String username) {
        return success(userService.getByUsername(username));
    }

    /**
     * 新增用户
     * 密码会自动使用 BCrypt 加密
     */
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestBody User user) {
        // 对密码进行 BCrypt 加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        return success(userService.save(user));
    }

    /**
     * 修改用户
     */
    @PutMapping("/update")
    public CommonResult<Boolean> update(@RequestBody User user) {
        return success(userService.updateById(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        return success(userService.removeById(id));
    }

    /**
     * 根据用户ID查询角色ID列表
     */
    @GetMapping("/{userId}/roles")
    public CommonResult<List<Long>> listRoleIdsByUserId(@PathVariable Long userId) {
        return success(userRoleService.listRoleIdsByUserId(userId));
    }

    /**
     * 分配用户角色
     */
    @PostMapping("/{userId}/roles")
    public CommonResult<Boolean> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return success(userRoleService.assignRoles(userId, roleIds));
    }

    /**
     * 根据用户ID查询角色列表（RBAC）
     */
    @GetMapping("/{userId}/roleList")
    public CommonResult<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        return success(userService.getRolesByUserId(userId));
    }

    /**
     * 根据用户ID查询角色编码列表（RBAC）
     */
    @GetMapping("/{userId}/roleCodes")
    public CommonResult<List<String>> getRoleCodesByUserId(@PathVariable Long userId) {
        return success(userService.getRoleCodesByUserId(userId));
    }

    /**
     * 根据用户ID查询权限列表（RBAC）
     */
    @GetMapping("/{userId}/permissionList")
    public CommonResult<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        return success(userService.getPermissionsByUserId(userId));
    }

    /**
     * 根据用户ID查询权限标识列表（RBAC）
     */
    @GetMapping("/{userId}/permissionCodes")
    public CommonResult<List<String>> getPermissionCodesByUserId(@PathVariable Long userId) {
        return success(userService.getPermissionCodesByUserId(userId));
    }
}
