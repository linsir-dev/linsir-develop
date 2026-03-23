package com.linsir.spring.framework.spring_core.annotation.support;

import com.linsir.spring.framework.spring_core.annotation.meta.Component;
import com.linsir.spring.framework.spring_core.annotation.meta.Scope;
import com.linsir.spring.framework.spring_core.annotation.meta.Transactional;

import java.lang.annotation.*;

/**
 * 服务门面组合注解
 *
 * 这是一个组合注解示例，同时具有 @Component、@Scope("prototype") 和 @Transactional 的效果。
 * 用于标识一个服务门面类，每次请求都会创建新实例，并且方法在事务中执行。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope("prototype")
@Transactional
public @interface ServiceFacade {

    /**
     * 组件名称
     * 映射到 @Component 的 value 属性
     *
     * @return 组件名称
     */
    String value() default "";

    /**
     * 事务传播行为
     * 映射到 @Transactional 的 propagation 属性
     *
     * @return 传播行为
     */
    Transactional.Propagation propagation() default Transactional.Propagation.REQUIRED;

    /**
     * 事务隔离级别
     * 映射到 @Transactional 的 isolation 属性
     *
     * @return 隔离级别
     */
    Transactional.Isolation isolation() default Transactional.Isolation.DEFAULT;

    /**
     * 是否为只读事务
     *
     * @return 是否只读
     */
    boolean readOnly() default false;
}
