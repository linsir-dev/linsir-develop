package com.linsir.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linsir.security.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 继承 BaseMapper 获得通用 CRUD 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 无需自定义方法，使用 MyBatis Plus 提供的通用方法即可
}
