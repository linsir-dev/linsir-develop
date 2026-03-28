package com.linsir.abc.core.jvm.compile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动生成toString方法的注解
 * <p>
 * 该注解用于类级别，编译时自动生成toString()方法的实现。
 * 支持排除特定字段和包含父类字段的选项。
 * </p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoToString {

    /**
     * 是否包含父类字段
     * <p>
     * 默认为false，只包含当前类声明的字段。
     * 设置为true时，会递归包含父类的非静态字段。
     * </p>
     *
     * @return true表示包含父类字段，false表示不包含
     */
    boolean includeSuper() default false;

    /**
     * 排除的字段名数组
     * <p>
     * 指定不需要包含在toString输出中的字段名称。
     * 常用于排除敏感信息（如密码、密钥等）。
     * </p>
     *
     * @return 需要排除的字段名数组
     */
    String[] exclude() default {};
}
