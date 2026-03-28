package com.linsir.abc.core.jvm.tuning.cluster;

/**
 * 集群消息类型枚举
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public enum MessageType {

    /**
     * 缓存同步消息
     */
    CACHE_SYNC,

    /**
     * 心跳消息
     */
    HEARTBEAT,

    /**
     * 节点加入
     */
    NODE_JOIN,

    /**
     * 节点离开
     */
    NODE_LEAVE,

    /**
     * 配置更新
     */
    CONFIG_UPDATE,

    /**
     * 状态请求
     */
    STATE_REQUEST,

    /**
     * 状态响应
     */
    STATE_RESPONSE
}
