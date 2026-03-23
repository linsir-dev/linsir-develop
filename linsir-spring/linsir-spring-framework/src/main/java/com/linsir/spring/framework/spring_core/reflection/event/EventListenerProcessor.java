package com.linsir.spring.framework.spring_core.reflection.event;

import com.linsir.spring.framework.spring_core.reflection.utils.ReflectionUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件监听器处理器
 * 模拟 Spring 的事件监听机制
 *
 * 核心功能：
 * 1. 注册事件监听器
 * 2. 根据事件类型分发事件
 * 3. 通过反射调用监听方法
 */
public class EventListenerProcessor {

    /**
     * 监听器注册表
     * Key: 事件类型
     * Value: 监听器方法列表
     */
    private final Map<Class<? extends ApplicationEvent>, List<ListenerMethod>> listenerRegistry = new ConcurrentHashMap<>();

    /**
     * 监听器方法封装
     */
    private static class ListenerMethod {
        final Object target;      // 监听器对象
        final Method method;      // 监听方法
        final int order;          // 执行顺序

        ListenerMethod(Object target, Method method, int order) {
            this.target = target;
            this.method = method;
            this.order = order;
        }
    }

    /**
     * 注册监听器对象
     * 扫描对象中所有标记 @EventListener 的方法
     *
     * @param listener 监听器对象
     */
    public void registerListener(Object listener) {
        Assert.notNull(listener, "Listener must not be null");
        Class<?> listenerClass = listener.getClass();

        // 遍历所有方法，查找标记 @EventListener 的方法
        ReflectionUtils.doWithMethods(listenerClass, method -> {
            if (method.isAnnotationPresent(EventListener.class)) {
                registerListenerMethod(listener, method);
            }
        });
    }

    /**
     * 注册单个监听方法
     *
     * @param target 监听器对象
     * @param method 监听方法
     */
    private void registerListenerMethod(Object target, Method method) {
        EventListener annotation = method.getAnnotation(EventListener.class);
        int order = annotation.order();

        // 获取方法参数
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            throw new IllegalArgumentException(
                "Event listener method must have exactly one parameter: " + method.getName()
            );
        }

        Class<?> paramType = paramTypes[0];

        // 检查参数类型是否为 ApplicationEvent 或其子类
        if (!ApplicationEvent.class.isAssignableFrom(paramType)) {
            throw new IllegalArgumentException(
                "Event listener method parameter must be ApplicationEvent or its subclass: " + method.getName()
            );
        }

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEvent> eventType = (Class<? extends ApplicationEvent>) paramType;

        // 如果注解中指定了事件类型，使用注解指定的类型
        if (annotation.value().length > 0) {
            for (Class<? extends ApplicationEvent> type : annotation.value()) {
                addListener(type, new ListenerMethod(target, method, order));
            }
        } else {
            // 使用参数类型作为事件类型
            addListener(eventType, new ListenerMethod(target, method, order));
        }
    }

    /**
     * 添加监听器到注册表
     *
     * @param eventType 事件类型
     * @param listener  监听器方法封装
     */
    private void addListener(Class<? extends ApplicationEvent> eventType, ListenerMethod listener) {
        listenerRegistry.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                       .add(listener);
        // 按 order 排序
        listenerRegistry.get(eventType).sort(Comparator.comparingInt(l -> l.order));
    }

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public void publishEvent(ApplicationEvent event) {
        Assert.notNull(event, "Event must not be null");

        // 获取事件类型
        Class<? extends ApplicationEvent> eventType = event.getClass();

        // 查找匹配的监听器
        List<ListenerMethod> listeners = findListeners(eventType);

        // 调用监听器方法
        for (ListenerMethod listener : listeners) {
            invokeListener(listener, event);
        }
    }

    /**
     * 查找匹配的监听器
     * 包括精确匹配和父类事件匹配
     *
     * @param eventType 事件类型
     * @return 监听器列表
     */
    private List<ListenerMethod> findListeners(Class<? extends ApplicationEvent> eventType) {
        List<ListenerMethod> result = new ArrayList<>();

        // 查找精确匹配
        List<ListenerMethod> exactListeners = listenerRegistry.get(eventType);
        if (exactListeners != null) {
            result.addAll(exactListeners);
        }

        // 查找父类事件匹配
        for (Map.Entry<Class<? extends ApplicationEvent>, List<ListenerMethod>> entry : listenerRegistry.entrySet()) {
            Class<? extends ApplicationEvent> registeredType = entry.getKey();
            if (registeredType != eventType && registeredType.isAssignableFrom(eventType)) {
                result.addAll(entry.getValue());
            }
        }

        // 按 order 排序
        result.sort(Comparator.comparingInt(l -> l.order));

        return result;
    }

    /**
     * 调用监听器方法
     *
     * @param listener 监听器方法封装
     * @param event    事件对象
     */
    private void invokeListener(ListenerMethod listener, ApplicationEvent event) {
        try {
            ReflectionUtils.invokeMethod(listener.method, listener.target, event);
        } catch (Exception e) {
            System.err.println("Failed to invoke event listener: " + listener.method.getName());
            e.printStackTrace();
        }
    }

    /**
     * 移除监听器对象的所有方法
     *
     * @param listener 监听器对象
     */
    public void unregisterListener(Object listener) {
        Assert.notNull(listener, "Listener must not be null");

        for (List<ListenerMethod> listeners : listenerRegistry.values()) {
            listeners.removeIf(l -> l.target == listener);
        }
    }

    /**
     * 清空所有监听器
     */
    public void clearListeners() {
        listenerRegistry.clear();
    }

    /**
     * 获取监听器数量
     *
     * @return 监听器数量
     */
    public int getListenerCount() {
        return listenerRegistry.values().stream()
                              .mapToInt(List::size)
                              .sum();
    }

    /**
     * 获取注册的事件类型数量
     *
     * @return 事件类型数量
     */
    public int getEventTypeCount() {
        return listenerRegistry.size();
    }
}
