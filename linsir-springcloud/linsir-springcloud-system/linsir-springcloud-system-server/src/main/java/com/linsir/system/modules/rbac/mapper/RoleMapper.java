package com.linsir.system.modules.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linsir.system.modules.rbac.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 *
 * @author linsir
 * @version 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
