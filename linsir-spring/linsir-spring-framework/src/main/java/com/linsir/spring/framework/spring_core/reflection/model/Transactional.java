package com.linsir.spring.framework.spring_core.reflection.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义 Transactional 注解
 * 用于事务管理示例
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

    /**
     * 事务传播行为
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * 事务隔离级别
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * 是否只读
     */
    boolean readOnly() default false;

    /**
     * 回滚异常
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * 传播行为枚举
     */
    enum Propagation {
        REQUIRED,    // 默认，如果存在事务则加入，否则新建
        SUPPORTS,    // 如果存在事务则加入，否则非事务执行
        MANDATORY,   // 必须存在事务，否则抛出异常
        REQUIRES_NEW,// 新建事务，如果存在则挂起当前事务
        NOT_SUPPORTED,// 非事务执行，如果存在则挂起当前事务
        NEVER,       // 非事务执行，如果存在事务则抛出异常
        NESTED       // 嵌套事务
    }

    /**
     * 隔离级别枚举
     */
    enum Isolation {
        DEFAULT,        // 使用数据库默认隔离级别
        READ_UNCOMMITTED,
        READ_COMMITTED,
        REPEATABLE_READ,
        SERIALIZABLE
    }
}
