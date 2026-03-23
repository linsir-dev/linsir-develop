package com.linsir.spring.framework.spring_core.annotation.support;

import java.lang.annotation.*;

/**
 * 定时任务注解
 *
 * 标识一个方法需要定时执行。
 * 支持 Cron 表达式、固定延迟和固定频率三种方式。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {

    /**
     * Cron 表达式
     * 例如："0 0 * * * *" 表示每小时执行一次
     *
     * @return Cron 表达式
     */
    String cron() default "";

    /**
     * 固定延迟（毫秒）
     * 上次执行完毕后等待指定时间再执行
     *
     * @return 延迟时间
     */
    long fixedDelay() default -1;

    /**
     * 固定频率（毫秒）
     * 每隔指定时间执行一次
     *
     * @return 频率
     */
    long fixedRate() default -1;

    /**
     * 初始延迟（毫秒）
     * 首次执行前的等待时间
     *
     * @return 初始延迟
     */
    long initialDelay() default -1;

    /**
     * 时区
     * 用于解析 Cron 表达式
     *
     * @return 时区
     */
    String zone() default "";
}
