package com.linsir.subject.sms.mapper;

import com.linsir.subject.sms.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author linsir
 * @since 2023-12-12
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
