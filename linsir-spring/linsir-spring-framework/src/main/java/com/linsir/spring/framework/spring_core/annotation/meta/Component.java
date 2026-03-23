package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 组件注解
 *
 * 标识一个类为 Spring 管理的组件，是最基础的构造型注解。
 * 其他专门的构造型注解（如 @Service、@Repository）都应该元注解包含此注解。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * 组件名称
     * 如果不指定，将使用类名的首字母小写形式
     *
     * @return 组件名称
     */
    String value() default "";
}
