package com.linsir.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.security.entity.User;

/**
 * 用户 Service 接口
 * 继承 IService 获得通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表（支持搜索）
     *
     * @param page     页码
     * @param rows     每页大小
     * @param username 用户名（可选）
     * @param nickname 昵称（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    IPage<User> getUserPage(int page, int rows, String username, String nickname, Integer status);
}
