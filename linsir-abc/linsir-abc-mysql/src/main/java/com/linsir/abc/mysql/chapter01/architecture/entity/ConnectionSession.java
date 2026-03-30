package com.linsir.abc.mysql.chapter01.architecture.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 连接会话实体类
 * 对应数据库表：connection_sessions
 * 模拟MySQL连接管理，记录连接信息
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionSession {

    /**
     * 会话ID
     * 主键，自增
     */
    private Long id;

    /**
     * 会话标识
     * 唯一标识符，UUID格式
     */
    private String sessionId;

    /**
     * 用户ID
     * 关联users表
     */
    private Long userId;

    /**
     * 客户端主机
     */
    private String clientHost;

    /**
     * 客户端端口
     */
    private Integer clientPort;

    /**
     * 服务器主机
     */
    private String serverHost;

    /**
     * 当前数据库
     */
    private String databaseName;

    /**
     * 连接时间
     */
    private LocalDateTime connectionTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 状态
     * 0-断开，1-活跃，2-空闲
     */
    private Integer status;

    /**
     * 命令执行次数
     */
    private Integer commandCount;

    /**
     * 总执行时间(ms)
     */
    private Long totalExecuteTime;

    /**
     * 检查会话是否活跃
     *
     * @return true-活跃
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 增加命令计数
     */
    public void incrementCommandCount() {
        if (this.commandCount == null) {
            this.commandCount = 0;
        }
        this.commandCount++;
    }

    /**
     * 增加执行时间
     *
     * @param executeTime 执行时间(ms)
     */
    public void addExecuteTime(long executeTime) {
        if (this.totalExecuteTime == null) {
            this.totalExecuteTime = 0L;
        }
        this.totalExecuteTime += executeTime;
    }

    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }
}
