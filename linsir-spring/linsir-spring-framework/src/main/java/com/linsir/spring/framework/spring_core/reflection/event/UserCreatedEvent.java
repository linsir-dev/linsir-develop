package com.linsir.spring.framework.spring_core.reflection.event;

import com.linsir.spring.framework.spring_core.reflection.model.User;

/**
 * 用户创建事件
 */
public class UserCreatedEvent extends ApplicationEvent {

    /**
     * 创建的用户
     */
    private final User user;

    /**
     * 构造方法
     *
     * @param source 事件源
     * @param user   创建的用户
     */
    public UserCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    /**
     * 获取创建的用户
     *
     * @return 用户对象
     */
    public User getUser() {
        return user;
    }

    @Override
    public String getEventType() {
        return "USER_CREATED";
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "user=" + user +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
