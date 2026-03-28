package com.linsir.abc.core.jvm.tuning.cluster;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * 集群同步管理器
 * 管理分布式缓存系统节点间的数据同步
 * 解决集群间同步导致的内存溢出问题
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ClusterSyncManager {

    private static final Logger LOGGER = Logger.getLogger(ClusterSyncManager.class.getName());

    /**
     * 默认接收缓冲区大小
     */
    private static final int DEFAULT_RECV_BUF_SIZE = 10000;

    /**
     * 默认最大消息包大小（字节）
     */
    private static final int DEFAULT_MAX_BUNDLE_SIZE = 64 * 1024;

    /**
     * 默认流量控制信用值
     */
    private static final long DEFAULT_MAX_CREDITS = 2 * 1024 * 1024;

    /**
     * 默认最小阈值
     */
    private static final double DEFAULT_MIN_THRESHOLD = 0.4;

    /**
     * 消息队列
     */
    private final BoundedMessageQueue messageQueue;

    /**
     * 消息处理器线程池
     */
    private final ExecutorService messageProcessor;

    /**
     * 流量控制器
     */
    private final FlowController flowController;

    /**
     * 是否运行中
     */
    private final AtomicBoolean running;

    /**
     * 节点ID
     */
    private final String nodeId;

    public ClusterSyncManager(String nodeId) {
        this(nodeId, DEFAULT_RECV_BUF_SIZE);
    }

    public ClusterSyncManager(String nodeId, int queueCapacity) {
        this.nodeId = nodeId;
        this.messageQueue = new BoundedMessageQueue("cluster-sync-" + nodeId, queueCapacity);
        this.messageProcessor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "message-processor-" + (++count));
            }
        });
        this.flowController = new FlowController(DEFAULT_MAX_CREDITS, DEFAULT_MIN_THRESHOLD);
        this.running = new AtomicBoolean(false);
    }

    /**
     * 启动集群同步管理器
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            LOGGER.info("Starting ClusterSyncManager for node: " + nodeId);
            startMessageConsumer();
        }
    }

    /**
     * 停止集群同步管理器
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            LOGGER.info("Stopping ClusterSyncManager for node: " + nodeId);
            messageProcessor.shutdown();
            try {
                if (!messageProcessor.awaitTermination(5, TimeUnit.SECONDS)) {
                    messageProcessor.shutdownNow();
                }
            } catch (InterruptedException e) {
                messageProcessor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 接收消息
     * 异步处理，避免阻塞接收线程
     *
     * @param message 集群消息
     */
    public void receive(ClusterMessage message) {
        if (!running.get()) {
            LOGGER.warning("ClusterSyncManager not running, dropping message: " + message.getMessageId());
            return;
        }

        // 流量控制检查
        if (!flowController.tryAcquire(message.getSize())) {
            LOGGER.warning("Flow control limit reached, dropping message: " + message.getMessageId());
            return;
        }

        // 将消息加入队列
        boolean accepted = messageQueue.receive(message);
        if (accepted) {
            LOGGER.fine("Message accepted: " + message.getMessageId());
        }
    }

    /**
     * 启动消息消费者
     */
    private void startMessageConsumer() {
        Thread consumerThread = new Thread(() -> {
            while (running.get()) {
                try {
                    ClusterMessage message = messageQueue.poll();
                    if (message != null) {
                        // 异步处理消息
                        messageProcessor.submit(() -> processMessage(message));
                    } else {
                        // 队列为空，短暂休眠
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    LOGGER.severe("Error processing message: " + e.getMessage());
                }
            }
        }, "message-consumer-" + nodeId);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /**
     * 处理消息
     *
     * @param message 集群消息
     */
    private void processMessage(ClusterMessage message) {
        try {
            LOGGER.fine("Processing message: " + message.getMessageId());

            switch (message.getType()) {
                case CACHE_SYNC:
                    handleCacheSync(message);
                    break;
                case HEARTBEAT:
                    handleHeartbeat(message);
                    break;
                case NODE_JOIN:
                    handleNodeJoin(message);
                    break;
                case NODE_LEAVE:
                    handleNodeLeave(message);
                    break;
                default:
                    LOGGER.warning("Unknown message type: " + message.getType());
            }

            // 释放流量控制信用
            flowController.release(message.getSize());

        } catch (Exception e) {
            LOGGER.severe("Error processing message " + message.getMessageId() + ": " + e.getMessage());
        }
    }

    /**
     * 处理缓存同步消息
     *
     * @param message 集群消息
     */
    private void handleCacheSync(ClusterMessage message) {
        LOGGER.info("Handling cache sync for key: " + message.getMessageId());
        // 实际业务逻辑：更新本地缓存
    }

    /**
     * 处理心跳消息
     *
     * @param message 集群消息
     */
    private void handleHeartbeat(ClusterMessage message) {
        LOGGER.fine("Received heartbeat from: " + message.getSenderNodeId());
        // 实际业务逻辑：更新节点状态
    }

    /**
     * 处理节点加入消息
     *
     * @param message 集群消息
     */
    private void handleNodeJoin(ClusterMessage message) {
        LOGGER.info("Node joined: " + message.getSenderNodeId());
        // 实际业务逻辑：添加新节点
    }

    /**
     * 处理节点离开消息
     *
     * @param message 集群消息
     */
    private void handleNodeLeave(ClusterMessage message) {
        LOGGER.info("Node left: " + message.getSenderNodeId());
        // 实际业务逻辑：移除节点
    }

    /**
     * 获取统计信息
     *
     * @return 统计信息
     */
    public String getStatistics() {
        return String.format(
                "ClusterSyncManager[nodeId=%s, running=%s, %s, flowControl=%s]",
                nodeId,
                running.get(),
                messageQueue.getStatistics(),
                flowController.getStatistics()
        );
    }

    public BoundedMessageQueue getMessageQueue() {
        return messageQueue;
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * 流量控制器
     * 实现简单的流量控制机制
     */
    private static class FlowController {

        private final long maxCredits;
        private final double minThreshold;
        private long currentCredits;
        private final Object lock = new Object();

        public FlowController(long maxCredits, double minThreshold) {
            this.maxCredits = maxCredits;
            this.minThreshold = minThreshold;
            this.currentCredits = maxCredits;
        }

        /**
         * 尝试获取信用
         *
         * @param size 请求大小
         * @return 是否成功
         */
        public boolean tryAcquire(long size) {
            synchronized (lock) {
                if (currentCredits >= size) {
                    currentCredits -= size;
                    return true;
                }
                return false;
            }
        }

        /**
         * 释放信用
         *
         * @param size 释放大小
         */
        public void release(long size) {
            synchronized (lock) {
                currentCredits = Math.min(currentCredits + size, maxCredits);
            }
        }

        public String getStatistics() {
            synchronized (lock) {
                return String.format("credits=%d/%d, threshold=%.2f",
                        currentCredits, maxCredits, minThreshold);
            }
        }
    }
}
