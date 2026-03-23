package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 值注入注解
 *
 * 用于从配置属性中注入值到字段或方法参数。
 * 支持 SpEL 表达式和占位符。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * 属性值表达式
     * 支持以下格式：
     * - 字面量："hello"
     * - 占位符："${app.name}"
     * - SpEL："#{systemProperties['user.name']}"
     *
     * @return 值表达式
     */
    String value();
}
