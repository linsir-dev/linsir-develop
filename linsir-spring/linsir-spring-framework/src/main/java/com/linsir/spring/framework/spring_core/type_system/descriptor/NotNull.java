package com.linsir.spring.framework.spring_core.type_system.descriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NotNull annotation for TypeDescriptor demo
 * Demonstrates how annotations are associated with type descriptors
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "Value must not be null";
}
