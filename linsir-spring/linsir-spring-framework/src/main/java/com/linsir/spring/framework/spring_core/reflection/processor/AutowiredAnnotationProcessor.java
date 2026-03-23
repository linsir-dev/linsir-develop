package com.linsir.spring.framework.spring_core.reflection.processor;

import com.linsir.spring.framework.spring_core.reflection.model.Autowired;
import com.linsir.spring.framework.spring_core.reflection.utils.ReflectionUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 依赖注入处理器
 * 模拟 Spring 的 @Autowired 注解处理机制
 *
 * 核心功能：
 * 1. 扫描目标类中标记 @Autowired 的字段
 * 2. 从 Bean 容器中获取对应类型的实例
 * 3. 通过反射将 Bean 注入到字段中
 */
public class AutowiredAnnotationProcessor {

    /**
     * Bean 容器（模拟 Spring 的 ApplicationContext）
     * Key: Bean 类型
     * Value: Bean 实例
     */
    private final Map<Class<?>, Object> beanContainer = new ConcurrentHashMap<>();

    /**
     * 注册 Bean 到容器
     *
     * @param bean Bean 实例
     */
    public void registerBean(Object bean) {
        Assert.notNull(bean, "Bean must not be null");
        Class<?> clazz = bean.getClass();
        // 注册实际类型
        beanContainer.put(clazz, bean);
        // 注册所有接口类型
        for (Class<?> ifc : clazz.getInterfaces()) {
            beanContainer.put(ifc, bean);
        }
    }

    /**
     * 处理目标对象的依赖注入
     *
     * @param target 目标对象
     */
    public void process(Object target) {
        Assert.notNull(target, "Target must not be null");
        Class<?> targetClass = target.getClass();

        // 遍历所有字段（包含父类），查找标记 @Autowired 的字段
        ReflectionUtils.doWithFields(targetClass, field -> {
            if (field.isAnnotationPresent(Autowired.class)) {
                injectField(field, target);
            }
        });
    }

    /**
     * 注入单个字段
     *
     * @param field  目标字段
     * @param target 目标对象
     */
    private void injectField(Field field, Object target) {
        Autowired autowired = field.getAnnotation(Autowired.class);
        Class<?> fieldType = field.getType();

        // 从容器中获取 Bean
        Object bean = beanContainer.get(fieldType);

        if (bean == null && autowired.required()) {
            throw new IllegalStateException(
                "No bean of type [" + fieldType.getName() + "] found for field [" + 
                field.getName() + "] in class [" + target.getClass().getName() + "]"
            );
        }

        if (bean != null) {
            // 使用反射设置字段值
            ReflectionUtils.setField(field, target, bean);
        }
    }

    /**
     * 创建 Bean 并自动注入依赖
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型参数
     * @return 创建并注入后的 Bean 实例
     */
    public <T> T createBean(Class<T> clazz) {
        try {
            // 实例化 Bean
            T instance = clazz.getDeclaredConstructor().newInstance();
            // 处理依赖注入
            process(instance);
            // 注册到容器
            registerBean(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean of type [" + clazz.getName() + "]", e);
        }
    }

    /**
     * 获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型参数
     * @return Bean 实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beanContainer.get(clazz);
    }

    /**
     * 判断容器中是否包含指定类型的 Bean
     *
     * @param clazz Bean 类型
     * @return true 表示存在
     */
    public boolean containsBean(Class<?> clazz) {
        return beanContainer.containsKey(clazz);
    }

    /**
     * 清空容器
     */
    public void clear() {
        beanContainer.clear();
    }

    /**
     * 获取容器中 Bean 的数量
     *
     * @return Bean 数量
     */
    public int getBeanCount() {
        return beanContainer.size();
    }
}
