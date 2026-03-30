package com.linsir.abc.mysql.chapter01.architecture.mapper;

import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问接口
 * 对应数据库表：users
 *
 * 职责：
 * 1. 用户数据的CRUD操作
 * 2. 用户认证相关查询
 * 3. 用户信息更新
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM users ORDER BY id")
    List<User> findAll();

    /**
     * 根据状态查询用户
     *
     * @param status 状态
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY id")
    List<User> findByStatus(Integer status);

    /**
     * 根据角色查询用户
     *
     * @param role 角色
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE role = #{role} ORDER BY id")
    List<User> findByRole(String role);

    /**
     * 插入用户
     *
     * @param user 用户对象
     * @return 影响行数
     */
    @Insert("INSERT INTO users (username, password, email, phone, status, role, " +
            "last_login_time, last_login_ip, login_count, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{status}, #{role}, " +
            "#{lastLoginTime}, #{lastLoginIp}, #{loginCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 影响行数
     */
    @Update("UPDATE users SET username = #{username}, password = #{password}, " +
            "email = #{email}, phone = #{phone}, status = #{status}, role = #{role}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(User user);

    /**
     * 更新登录信息
     *
     * @param userId       用户ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp   最后登录IP
     * @param loginCount    登录次数
     * @return 影响行数
     */
    @Update("UPDATE users SET last_login_time = #{lastLoginTime}, " +
            "last_login_ip = #{lastLoginIp}, login_count = #{loginCount} " +
            "WHERE id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId,
                        @Param("lastLoginTime") LocalDateTime lastLoginTime,
                        @Param("lastLoginIp") String lastLoginIp,
                        @Param("loginCount") Integer loginCount);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计用户数量
     *
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM users")
    long count();

    /**
     * 根据用户名统计
     *
     * @param username 用户名
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
}
