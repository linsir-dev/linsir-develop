package com.linsir.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linsir.security.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 * 继承 BaseMapper 获得通用 CRUD 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
