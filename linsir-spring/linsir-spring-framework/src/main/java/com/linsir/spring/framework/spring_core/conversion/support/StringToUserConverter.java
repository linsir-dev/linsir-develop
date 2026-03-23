package com.linsir.spring.framework.spring_core.conversion.support;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;

/**
 * 字符串转用户对象转换器
 * 用于演示自定义转换器的实现
 *
 * <p>转换格式: "name,age" 或 "name,age,email"</p>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class StringToUserConverter implements Converter<String, User> {

    @Override
    public User convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        String[] parts = source.split(",");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid user format. Expected: name,age or name,age,email");
        }

        User user = new User();
        user.setName(parts[0].trim());
        user.setAge(Integer.parseInt(parts[1].trim()));

        if (parts.length > 2) {
            user.setEmail(parts[2].trim());
        }

        return user;
    }
}
