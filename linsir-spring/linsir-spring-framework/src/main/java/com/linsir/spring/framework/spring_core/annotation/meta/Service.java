package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 服务层组件注解
 *
 * 标识一个类为业务逻辑层（Service Layer）的组件。
 * 这是 @Component 的特化形式，用于标识服务层类。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    /**
     * 服务组件名称
     * 如果不指定，将使用类名的首字母小写形式
     *
     * @return 组件名称
     */
    String value() default "";
}
