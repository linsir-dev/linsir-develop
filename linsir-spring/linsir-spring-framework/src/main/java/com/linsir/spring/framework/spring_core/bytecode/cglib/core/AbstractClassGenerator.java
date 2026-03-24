package com.linsir.spring.framework.spring_core.bytecode.cglib.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象类生成器
 *
 * <p>CGLIB类生成的基类，定义了类生成的标准流程和通用逻辑。
 * 所有具体的类生成器（如Enhancer）都继承此类。
 *
 * <p>主要职责：
 * <ul>
 *   <li>生成唯一的类名</li>
 *   <li>管理类加载器</li>
 *   <li>提供生成策略扩展点</li>
 *   <li>缓存生成的类</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see Enhancer
 * @see GeneratorStrategy
 */
public abstract class AbstractClassGenerator {

    /**
     * 类名计数器，用于生成唯一的类名
     */
    private static final AtomicInteger classNameCounter = new AtomicInteger(0);

    /**
     * 类名前缀
     */
    private static final String CLASS_NAME_PREFIX = "com.linsir.cglib.proxy";

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    /**
     * 生成策略
     */
    private GeneratorStrategy strategy;

    /**
     * 命名策略
     */
    private NamingPolicy namingPolicy = new DefaultNamingPolicy();

    /**
     * 生成类名
     *
     * @param superclass 父类
     * @return 生成的类名
     */
    protected String generateClassName(Class<?> superclass) {
        if (namingPolicy != null) {
            return namingPolicy.getClassName(superclass.getName(),
                CLASS_NAME_PREFIX,
                classNameCounter.getAndIncrement());
        }

        // 默认命名规则
        return CLASS_NAME_PREFIX + "." +
               superclass.getSimpleName() + "$$EnhancerByCGLIB$$" +
               Integer.toHexString(classNameCounter.getAndIncrement());
    }

    /**
     * 生成类
     *
     * @return 生成的类
     */
    protected abstract Class<?> generateClass();

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return classLoader;
    }

    /**
     * 设置类加载器
     *
     * @param classLoader 类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 获取生成策略
     *
     * @return 生成策略
     */
    public GeneratorStrategy getStrategy() {
        if (strategy == null) {
            strategy = getDefaultGeneratorStrategy();
        }
        return strategy;
    }

    /**
     * 设置生成策略
     *
     * @param strategy 生成策略
     */
    public void setStrategy(GeneratorStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 获取默认生成策略
     *
     * @return 默认生成策略
     */
    protected abstract GeneratorStrategy getDefaultGeneratorStrategy();

    /**
     * 获取命名策略
     *
     * @return 命名策略
     */
    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    /**
     * 设置命名策略
     *
     * @param namingPolicy 命名策略
     */
    public void setNamingPolicy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
    }

    /**
     * 命名策略接口
     */
    public interface NamingPolicy {
        /**
         * 获取类名
         *
         * @param className 原始类名
         * @param prefix 前缀
         * @param counter 计数器
         * @return 生成的类名
         */
        String getClassName(String className, String prefix, int counter);
    }

    /**
     * 默认命名策略
     */
    private static class DefaultNamingPolicy implements NamingPolicy {
        @Override
        public String getClassName(String className, String prefix, int counter) {
            String baseName = className.substring(className.lastIndexOf('.') + 1);
            return prefix + "." + baseName + "$$EnhancerByCGLIB$$" +
                   Integer.toHexString(counter);
        }
    }
}
