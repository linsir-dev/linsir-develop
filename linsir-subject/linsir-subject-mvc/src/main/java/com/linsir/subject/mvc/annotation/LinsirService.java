package com.linsir.subject.mvc.annotation;

import java.lang.annotation.*;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/1 23:10
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinsirService {
    String value() default "";
}
