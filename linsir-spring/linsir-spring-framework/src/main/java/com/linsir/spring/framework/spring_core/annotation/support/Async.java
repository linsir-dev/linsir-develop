package com.linsir.spring.framework.spring_core.annotation.support;

import java.lang.annotation.*;

/**
 * 异步方法注解
 *
 * 标识一个方法需要异步执行。
 * 被标记的方法将在单独的线程中执行，调用者立即返回。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {

    /**
     * 执行器名称
     * 指定使用哪个 TaskExecutor 执行异步任务
     *
     * @return 执行器名称
     */
    String value() default "";
}
