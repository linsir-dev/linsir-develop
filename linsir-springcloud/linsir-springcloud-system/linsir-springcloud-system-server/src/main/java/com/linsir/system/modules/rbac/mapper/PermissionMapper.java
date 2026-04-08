package com.linsir.system.modules.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linsir.system.modules.rbac.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper 接口
 *
 * @author linsir
 * @version 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
