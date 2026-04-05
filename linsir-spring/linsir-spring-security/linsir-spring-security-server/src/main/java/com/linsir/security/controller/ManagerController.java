package com.linsir.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 * 
 * 处理后台管理相关的页面请求
 * 
 * @author linsir
 * @version 1.0.0
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {

    /**
     * 后台管理首页
     *
     * @return 后台管理框架视图
     */
    @GetMapping("")
    public String index() {
        return "manager/index";
    }

    /**
     * 后台管理仪表板页面
     *
     * @return 仪表板视图
     */
    @GetMapping("/home")
    public String home() {
        return "manager/home";
    }

    /**
     * 用户列表页面
     *
     * @return 用户列表视图
     */
    @GetMapping("/user/list")
    public String userList() {
        return "manager/user/list";
    }

    /**
     * 角色列表页面
     *
     * @return 角色列表视图
     */
    @GetMapping("/role/list")
    public String roleList() {
        return "manager/role/list";
    }

    /**
     * 权限列表页面
     *
     * @return 权限列表视图
     */
    @GetMapping("/permission/list")
    public String permissionList() {
        return "manager/permission/list";
    }

    /**
     * 获取导航菜单数据
     *
     * @return 导航菜单 JSON 数据
     */
    @GetMapping("/nav-data")
    @ResponseBody
    public List<Map<String, Object>> getNavData() {
        List<Map<String, Object>> navList = new ArrayList<>();
        
        // 系统管理
        Map<String, Object> system = new HashMap<>();
        system.put("id", "system");
        system.put("text", "系统管理");
        system.put("iconCls", "icon-save");
        
        List<Map<String, Object>> systemChildren = new ArrayList<>();
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("id", "dashboard");
        dashboard.put("text", "系统首页");
        dashboard.put("iconCls", "icon-home");
        dashboard.put("url", "/manager/dashboard");
        systemChildren.add(dashboard);
        
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("id", "system-info");
        systemInfo.put("text", "系统信息");
        systemInfo.put("iconCls", "icon-info");
        systemInfo.put("url", "/manager/system-info");
        systemChildren.add(systemInfo);
        
        system.put("children", systemChildren);
        navList.add(system);
        
        // 用户管理
        Map<String, Object> user = new HashMap<>();
        user.put("id", "user");
        user.put("text", "用户管理");
        user.put("iconCls", "icon-users");
        
        List<Map<String, Object>> userChildren = new ArrayList<>();
        Map<String, Object> userList = new HashMap<>();
        userList.put("id", "user-list");
        userList.put("text", "用户列表");
        userList.put("iconCls", "icon-edit");
        userList.put("url", "/manager/user/list");
        userChildren.add(userList);
        
        Map<String, Object> userAdd = new HashMap<>();
        userAdd.put("id", "user-add");
        userAdd.put("text", "添加用户");
        userAdd.put("iconCls", "icon-add");
        userAdd.put("url", "/manager/user/add");
        userChildren.add(userAdd);
        
        Map<String, Object> userRole = new HashMap<>();
        userRole.put("id", "user-role");
        userRole.put("text", "角色分配");
        userRole.put("iconCls", "icon-filter");
        userRole.put("url", "/manager/user/role");
        userChildren.add(userRole);
        
        user.put("children", userChildren);
        navList.add(user);
        
        // 角色权限
        Map<String, Object> role = new HashMap<>();
        role.put("id", "role");
        role.put("text", "角色权限");
        role.put("iconCls", "icon-lock");

        List<Map<String, Object>> roleChildren = new ArrayList<>();
        Map<String, Object> roleList = new HashMap<>();
        roleList.put("id", "role-list");
        roleList.put("text", "角色列表");
        roleList.put("iconCls", "icon-edit");
        roleList.put("url", "/manager/role/list");
        roleChildren.add(roleList);

        Map<String, Object> permissionList = new HashMap<>();
        permissionList.put("id", "permission-list");
        permissionList.put("text", "权限列表");
        permissionList.put("iconCls", "icon-edit");
        permissionList.put("url", "/manager/permission/list");
        roleChildren.add(permissionList);

        role.put("children", roleChildren);
        navList.add(role);
        
        // 系统设置
        Map<String, Object> settings = new HashMap<>();
        settings.put("id", "settings");
        settings.put("text", "系统设置");
        settings.put("iconCls", "icon-settings");
        
        List<Map<String, Object>> settingsChildren = new ArrayList<>();
        Map<String, Object> settingsBasic = new HashMap<>();
        settingsBasic.put("id", "settings-basic");
        settingsBasic.put("text", "基本设置");
        settingsBasic.put("iconCls", "icon-save");
        settingsBasic.put("url", "/manager/settings/basic");
        settingsChildren.add(settingsBasic);
        
        Map<String, Object> settingsSecurity = new HashMap<>();
        settingsSecurity.put("id", "settings-security");
        settingsSecurity.put("text", "安全设置");
        settingsSecurity.put("iconCls", "icon-lock");
        settingsSecurity.put("url", "/manager/settings/security");
        settingsChildren.add(settingsSecurity);
        
        Map<String, Object> settingsLog = new HashMap<>();
        settingsLog.put("id", "settings-log");
        settingsLog.put("text", "日志管理");
        settingsLog.put("iconCls", "icon-search");
        settingsLog.put("url", "/manager/settings/log");
        settingsChildren.add(settingsLog);
        
        settings.put("children", settingsChildren);
        navList.add(settings);
        
        return navList;
    }
}
