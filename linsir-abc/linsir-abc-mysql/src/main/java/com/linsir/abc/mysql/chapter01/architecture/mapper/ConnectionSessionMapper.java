package com.linsir.abc.mysql.chapter01.architecture.mapper;

import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 连接会话数据访问接口
 * 对应数据库表：connection_sessions
 *
 * 职责：
 * 1. 会话数据的CRUD操作
 * 2. 会话状态管理
 * 3. 会话统计查询
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface ConnectionSessionMapper {

    /**
     * 根据ID查询会话
     *
     * @param id 会话ID
     * @return 会话对象
     */
    @Select("SELECT * FROM connection_sessions WHERE id = #{id}")
    ConnectionSession findById(Long id);

    /**
     * 根据会话ID查询
     *
     * @param sessionId 会话标识
     * @return 会话对象
     */
    @Select("SELECT * FROM connection_sessions WHERE session_id = #{sessionId}")
    ConnectionSession findBySessionId(String sessionId);

    /**
     * 根据用户ID查询会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM connection_sessions WHERE user_id = #{userId} ORDER BY connection_time DESC")
    List<ConnectionSession> findByUserId(Long userId);

    /**
     * 查询所有会话
     *
     * @return 会话列表
     */
    @Select("SELECT * FROM connection_sessions ORDER BY connection_time DESC")
    List<ConnectionSession> findAll();

    /**
     * 根据状态查询会话
     *
     * @param status 状态
     * @return 会话列表
     */
    @Select("SELECT * FROM connection_sessions WHERE status = #{status} ORDER BY connection_time DESC")
    List<ConnectionSession> findByStatus(Integer status);

    /**
     * 插入会话
     *
     * @param session 会话对象
     * @return 影响行数
     */
    @Insert("INSERT INTO connection_sessions (session_id, user_id, client_host, client_port, server_host, " +
            "database_name, connection_time, last_active_time, status, command_count, total_execute_time) " +
            "VALUES (#{sessionId}, #{userId}, #{clientHost}, #{clientPort}, #{serverHost}, " +
            "#{databaseName}, #{connectionTime}, #{lastActiveTime}, #{status}, #{commandCount}, #{totalExecuteTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConnectionSession session);

    /**
     * 更新会话状态
     *
     * @param sessionId 会话标识
     * @param status    新状态
     * @return 影响行数
     */
    @Update("UPDATE connection_sessions SET status = #{status} WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") Integer status);

    /**
     * 更新最后活跃时间
     *
     * @param sessionId      会话标识
     * @param lastActiveTime 最后活跃时间
     * @return 影响行数
     */
    @Update("UPDATE connection_sessions SET last_active_time = #{lastActiveTime} WHERE session_id = #{sessionId}")
    int updateActiveTime(@Param("sessionId") String sessionId, @Param("lastActiveTime") LocalDateTime lastActiveTime);

    /**
     * 更新命令统计
     *
     * @param sessionId       会话标识
     * @param commandCount    命令次数
     * @param totalExecuteTime 总执行时间
     * @return 影响行数
     */
    @Update("UPDATE connection_sessions SET command_count = #{commandCount}, " +
            "total_execute_time = #{totalExecuteTime} WHERE session_id = #{sessionId}")
    int updateStats(@Param("sessionId") String sessionId,
                    @Param("commandCount") Integer commandCount,
                    @Param("totalExecuteTime") Long totalExecuteTime);

    /**
     * 删除会话
     *
     * @param id 会话ID
     * @return 影响行数
     */
    @Delete("DELETE FROM connection_sessions WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计会话数量
     *
     * @return 会话数量
     */
    @Select("SELECT COUNT(*) FROM connection_sessions")
    long count();

    /**
     * 统计活跃会话数量
     *
     * @return 活跃会话数量
     */
    @Select("SELECT COUNT(*) FROM connection_sessions WHERE status = 1")
    long countActive();
}
