package com.linsir.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linsir.security.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper 接口
 * 继承 BaseMapper 获得通用 CRUD 方法
 *
 * @author linsir
 * @version 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
