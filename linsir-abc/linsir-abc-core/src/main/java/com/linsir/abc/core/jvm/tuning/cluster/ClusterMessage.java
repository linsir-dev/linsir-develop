package com.linsir.abc.core.jvm.tuning.cluster;

import java.io.Serializable;
import java.time.Instant;

/**
 * 集群消息类
 * 用于在分布式缓存系统节点间传输数据
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ClusterMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一标识
     */
    private final String messageId;

    /**
     * 消息类型
     */
    private final MessageType type;

    /**
     * 发送节点ID
     */
    private final String senderNodeId;

    /**
     * 目标节点ID（null表示广播）
     */
    private final String targetNodeId;

    /**
     * 消息内容
     */
    private final byte[] payload;

    /**
     * 消息大小（字节）
     */
    private final int size;

    /**
     * 创建时间戳
     */
    private final Instant createTime;

    /**
     * 超时时间（毫秒）
     */
    private final long timeoutMillis;

    public ClusterMessage(String messageId, MessageType type, String senderNodeId,
                          String targetNodeId, byte[] payload, long timeoutMillis) {
        this.messageId = messageId;
        this.type = type;
        this.senderNodeId = senderNodeId;
        this.targetNodeId = targetNodeId;
        this.payload = payload;
        this.size = payload != null ? payload.length : 0;
        this.createTime = Instant.now();
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * 检查消息是否已超时
     *
     * @return 是否超时
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - createTime.toEpochMilli() > timeoutMillis;
    }

    /**
     * 创建缓存同步消息
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return 集群消息
     */
    public static ClusterMessage createCacheSyncMessage(String key, byte[] value) {
        String messageId = key + "_" + System.currentTimeMillis();
        return new ClusterMessage(
                messageId,
                MessageType.CACHE_SYNC,
                "current-node",
                null,
                value,
                30000
        );
    }

    public String getMessageId() {
        return messageId;
    }

    public MessageType getType() {
        return type;
    }

    public String getSenderNodeId() {
        return senderNodeId;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getSize() {
        return size;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    @Override
    public String toString() {
        return "ClusterMessage{" +
                "messageId='" + messageId + '\'' +
                ", type=" + type +
                ", senderNodeId='" + senderNodeId + '\'' +
                ", size=" + size +
                ", createTime=" + createTime +
                '}';
    }
}
