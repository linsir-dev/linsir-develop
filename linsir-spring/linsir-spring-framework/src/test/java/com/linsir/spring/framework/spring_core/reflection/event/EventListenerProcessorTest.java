package com.linsir.spring.framework.spring_core.reflection.event;

import com.linsir.spring.framework.spring_core.reflection.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EventListenerProcessor 测试类
 * 测试事件监听处理器的功能
 */
@DisplayName("EventListenerProcessor 事件监听测试")
class EventListenerProcessorTest {

    private EventListenerProcessor processor;
    private List<String> eventLog;

    @BeforeEach
    void setUp() {
        processor = new EventListenerProcessor();
        eventLog = new ArrayList<>();
    }

    @Test
    @DisplayName("测试注册监听器")
    void testRegisterListener() {
        // 创建监听器
        UserEventListener listener = new UserEventListener();

        // 注册监听器
        processor.registerListener(listener);

        // 验证注册成功
        assertEquals(1, processor.getListenerCount(), "应该注册 1 个监听器方法");
    }

    @Test
    @DisplayName("测试发布事件 - 单个监听器")
    void testPublishEvent_SingleListener() {
        // 创建并注册监听器
        UserEventListener listener = new UserEventListener();
        processor.registerListener(listener);

        // 创建事件
        User user = new User();
        user.setUsername("testUser");
        UserCreatedEvent event = new UserCreatedEvent(this, user);

        // 发布事件
        processor.publishEvent(event);

        // 验证监听器被调用
        assertTrue(listener.isUserCreatedCalled(), "监听器应该被调用");
        assertEquals(user, listener.getLastUser(), "应该接收到正确的用户");
    }

    @Test
    @DisplayName("测试发布事件 - 多个监听器")
    void testPublishEvent_MultipleListeners() {
        // 创建多个监听器
        UserEventListener listener1 = new UserEventListener();
        LoggingEventListener listener2 = new LoggingEventListener();

        processor.registerListener(listener1);
        processor.registerListener(listener2);

        // 发布事件
        User user = new User();
        user.setUsername("testUser");
        UserCreatedEvent event = new UserCreatedEvent(this, user);
        processor.publishEvent(event);

        // 验证两个监听器都被调用
        assertTrue(listener1.isUserCreatedCalled(), "监听器1应该被调用");
        assertTrue(listener2.isEventLogged(), "监听器2应该被调用");
    }

    @Test
    @DisplayName("测试监听器执行顺序")
    void testListenerOrder() {
        // 创建按顺序记录的监听器
        OrderTestListener listener = new OrderTestListener();
        processor.registerListener(listener);

        // 发布事件
        User user = new User();
        UserCreatedEvent event = new UserCreatedEvent(this, user);
        processor.publishEvent(event);

        // 验证执行顺序
        List<String> executionOrder = listener.getExecutionOrder();
        assertEquals(2, executionOrder.size(), "应该有两个监听器方法被调用");
        assertEquals("highPriority", executionOrder.get(0), "高优先级应该先执行");
        assertEquals("lowPriority", executionOrder.get(1), "低优先级应该后执行");
    }

    @Test
    @DisplayName("测试移除监听器")
    void testUnregisterListener() {
        // 创建并注册监听器
        UserEventListener listener = new UserEventListener();
        processor.registerListener(listener);
        assertEquals(1, processor.getListenerCount(), "注册后应该有 1 个监听器");

        // 移除监听器
        processor.unregisterListener(listener);
        assertEquals(0, processor.getListenerCount(), "移除后应该没有监听器");

        // 发布事件，监听器不应该被调用
        User user = new User();
        UserCreatedEvent event = new UserCreatedEvent(this, user);
        processor.publishEvent(event);

        assertFalse(listener.isUserCreatedCalled(), "移除后监听器不应该被调用");
    }

    @Test
    @DisplayName("测试清空监听器")
    void testClearListeners() {
        // 注册多个监听器
        processor.registerListener(new UserEventListener());
        processor.registerListener(new LoggingEventListener());
        assertTrue(processor.getListenerCount() > 0, "应该有监听器");

        // 清空
        processor.clearListeners();
        assertEquals(0, processor.getListenerCount(), "清空后应该没有监听器");
    }

    @Test
    @DisplayName("测试获取监听器数量")
    void testGetListenerCount() {
        assertEquals(0, processor.getListenerCount(), "初始应该没有监听器");

        processor.registerListener(new UserEventListener());
        assertEquals(1, processor.getListenerCount(), "注册后应该有 1 个监听器方法");
    }

    @Test
    @DisplayName("测试获取事件类型数量")
    void testGetEventTypeCount() {
        assertEquals(0, processor.getEventTypeCount(), "初始应该没有事件类型");

        processor.registerListener(new UserEventListener());
        assertEquals(1, processor.getEventTypeCount(), "应该有 1 个事件类型");
    }

    @Test
    @DisplayName("测试注册 null 监听器 - 应该抛出异常")
    void testRegisterListener_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            processor.registerListener(null);
        }, "注册 null 监听器应该抛出异常");
    }

    @Test
    @DisplayName("测试发布 null 事件 - 应该抛出异常")
    void testPublishEvent_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            processor.publishEvent(null);
        }, "发布 null 事件应该抛出异常");
    }

    // ==================== 测试用的监听器类 ====================

    /**
     * 用户事件监听器
     */
    static class UserEventListener {
        private boolean userCreatedCalled = false;
        private User lastUser;

        @EventListener
        public void onUserCreated(UserCreatedEvent event) {
            this.userCreatedCalled = true;
            this.lastUser = event.getUser();
        }

        public boolean isUserCreatedCalled() {
            return userCreatedCalled;
        }

        public User getLastUser() {
            return lastUser;
        }
    }

    /**
     * 日志事件监听器
     */
    static class LoggingEventListener {
        private boolean eventLogged = false;

        @EventListener
        public void logEvent(UserCreatedEvent event) {
            this.eventLogged = true;
            System.out.println("[LOG] User created: " + event.getUser().getUsername());
        }

        public boolean isEventLogged() {
            return eventLogged;
        }
    }

    /**
     * 测试执行顺序的监听器
     */
    static class OrderTestListener {
        private final List<String> executionOrder = new ArrayList<>();

        @EventListener(order = 1)  // 低优先级
        public void lowPriority(UserCreatedEvent event) {
            executionOrder.add("lowPriority");
        }

        @EventListener(order = 0)  // 高优先级
        public void highPriority(UserCreatedEvent event) {
            executionOrder.add("highPriority");
        }

        public List<String> getExecutionOrder() {
            return executionOrder;
        }
    }
}
