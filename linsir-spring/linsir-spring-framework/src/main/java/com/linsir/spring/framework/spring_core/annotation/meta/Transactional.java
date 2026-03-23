package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 事务注解
 *
 * 标识一个方法或类需要在事务中执行。
 * 可以配置事务的传播行为和隔离级别。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {

    /**
     * 事务传播行为
     *
     * @return 传播行为
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * 事务隔离级别
     *
     * @return 隔离级别
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * 事务超时时间（秒）
     * -1 表示使用默认超时
     *
     * @return 超时时间
     */
    int timeout() default -1;

    /**
     * 是否为只读事务
     *
     * @return 是否只读
     */
    boolean readOnly() default false;

    /**
     * 导致事务回滚的异常类
     *
     * @return 异常类数组
     */
    Class<? extends Exception>[] rollbackFor() default {};

    /**
     * 不会导致事务回滚的异常类
     *
     * @return 异常类数组
     */
    Class<? extends Exception>[] noRollbackFor() default {};

    /**
     * 事务传播行为枚举
     */
    enum Propagation {
        /**
         * 如果当前存在事务，则加入该事务；否则创建新事务
         */
        REQUIRED,

        /**
         * 如果当前存在事务，则加入该事务；否则以非事务方式执行
         */
        SUPPORTS,

        /**
         * 如果当前存在事务，则加入该事务；否则抛出异常
         */
        MANDATORY,

        /**
         * 创建新事务，如果当前存在事务则挂起当前事务
         */
        REQUIRES_NEW,

        /**
         * 以非事务方式执行，如果当前存在事务则挂起当前事务
         */
        NOT_SUPPORTED,

        /**
         * 以非事务方式执行，如果当前存在事务则抛出异常
         */
        NEVER,

        /**
         * 如果当前存在事务，则在嵌套事务中执行；否则创建新事务
         */
        NESTED
    }

    /**
     * 事务隔离级别枚举
     */
    enum Isolation {
        /**
         * 使用默认隔离级别
         */
        DEFAULT,

        /**
         * 读未提交
         */
        READ_UNCOMMITTED,

        /**
         * 读已提交
         */
        READ_COMMITTED,

        /**
         * 可重复读
         */
        REPEATABLE_READ,

        /**
         * 串行化
         */
        SERIALIZABLE
    }
}
