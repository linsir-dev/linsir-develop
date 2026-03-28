package com.linsir.abc.core.jvm.compile;

import com.linsir.abc.core.jvm.compile.test.User;

/**
 * 由AutoToStringProcessor自动生成的toString实现类
 * <p>
 * 该类为 {@link User} 提供toString()方法的实现。
 * </p>
 * <p><strong>注意：</strong>此类由注解处理器自动生成，请勿手动修改</p>
 *
 * @author AutoToStringProcessor
 * @version 1.0
 * @since 2026-03-28
 * @see User
 */
public class UserToStringImpl {

    /**
     * 私有构造方法，防止实例化
     */
    private UserToStringImpl() {
        throw new AssertionError("工具类不应被实例化");
    }

    /**
     * 生成目标对象的字符串表示
     * <p>
     * 返回格式：User{field1='value1', field2='value2', ...}
     * </p>
     *
     * @param obj 目标对象，不能为null
     * @return 对象的字符串表示
     * @throws NullPointerException 如果obj为null
     */
    public static String toString(User obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        sb.append("id=");
        sb.append(obj.getId());
        sb.append(", ");
        sb.append("username=");
        sb.append(obj.getUsername());
        sb.append(", ");
        sb.append("age=");
        sb.append(obj.getAge());
        sb.append('}');
        return sb.toString();
    }
}
