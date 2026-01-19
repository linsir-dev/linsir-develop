package com.linsir.spring.framework.ioc.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @ClassName : PersonValidator
 * @Description : 数据验证
 * @Author : Linsir
 * @Date: 2023-12-02 21:07
 */

public class PersonValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
