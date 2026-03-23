package com.linsir.spring.framework.spring_core.reflection.event;

import java.time.LocalDateTime;

/**
 * 应用事件基类
 * 所有应用事件都继承此类
 */
public abstract class ApplicationEvent {

    /**
     * 事件发生时间
     */
    private final LocalDateTime timestamp;

    /**
     * 事件源
     */
    private final Object source;

    /**
     * 构造方法
     *
     * @param source 事件源
     */
    public ApplicationEvent(Object source) {
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 获取事件源
     *
     * @return 事件源
     */
    public Object getSource() {
        return source;
    }

    /**
     * 获取事件发生时间
     *
     * @return 时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public abstract String getEventType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "timestamp=" + timestamp +
                ", source=" + source +
                ", type=" + getEventType() +
                '}';
    }
}
