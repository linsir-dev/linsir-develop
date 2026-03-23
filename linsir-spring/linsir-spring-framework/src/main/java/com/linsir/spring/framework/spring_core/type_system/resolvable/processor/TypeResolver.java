package com.linsir.spring.framework.spring_core.type_system.resolvable.processor;

import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 类型解析器
 * 使用Spring的ResolvableType API解析泛型类型
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class TypeResolver {

    /**
     * 解析类的泛型参数
     * 例如：UserService extends BaseService<User, Long>
     * 解析结果：User.class, Long.class
     *
     * @param clazz          要解析的类
     * @param genericBaseClass 泛型基类
     * @return 泛型参数数组
     */
    public static Class<?>[] resolveClassGenerics(Class<?> clazz, Class<?> genericBaseClass) {
        ResolvableType resolvableType = ResolvableType.forClass(clazz).as(genericBaseClass);
        ResolvableType[] generics = resolvableType.getGenerics();

        Class<?>[] result = new Class[generics.length];
        for (int i = 0; i < generics.length; i++) {
            result[i] = generics[i].resolve();
        }
        return result;
    }

    /**
     * 解析字段的泛型类型
     * 例如：private List<String> names;
     * 解析结果：String.class
     *
     * @param field 要解析的字段
     * @return 泛型类型
     */
    public static Class<?> resolveFieldGeneric(Field field) {
        ResolvableType fieldType = ResolvableType.forField(field);
        return fieldType.getGeneric(0).resolve();
    }

    /**
     * 解析字段的完整泛型信息
     *
     * @param field 要解析的字段
     * @return 泛型类型描述
     */
    public static String resolveFieldGenericInfo(Field field) {
        ResolvableType fieldType = ResolvableType.forField(field);
        return fieldType.toString();
    }

    /**
     * 解析方法返回类型的泛型
     * 例如：List<User> getUsers()
     * 解析结果：User.class
     *
     * @param method 要解析的方法
     * @return 返回类型的泛型参数
     */
    public static Class<?> resolveMethodReturnGeneric(Method method) {
        ResolvableType returnType = ResolvableType.forMethodReturnType(method);
        return returnType.getGeneric(0).resolve();
    }

    /**
     * 解析方法参数的泛型类型
     *
     * @param method 方法
     * @param index  参数索引
     * @return 参数的泛型类型
     */
    public static Class<?> resolveMethodParameterGeneric(Method method, int index) {
        ResolvableType parameterType = ResolvableType.forMethodParameter(method, index);
        return parameterType.getGeneric(0).resolve();
    }

    /**
     * 检查类型是否可分配
     * 例如：ArrayList 可以分配给 List
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 是否可分配
     */
    public static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        // 使用Java原生API检查类型可分配性
        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * 获取数组的组件类型
     *
     * @param arrayClass 数组类型
     * @return 组件类型
     */
    public static Class<?> getArrayComponentType(Class<?> arrayClass) {
        ResolvableType arrayType = ResolvableType.forClass(arrayClass);
        return arrayType.getComponentType().resolve();
    }

    /**
     * 使用传统反射方式解析泛型（对比用）
     *
     * @param clazz          类
     * @param genericBaseClass 泛型基类
     * @return 泛型参数数组
     */
    public static Type[] resolveGenericsTraditionally(Class<?> clazz, Class<?> genericBaseClass) {
        Type genericSuperclass = clazz.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            return paramType.getActualTypeArguments();
        }

        // 检查接口
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericInterface;
                if (paramType.getRawType() == genericBaseClass) {
                    return paramType.getActualTypeArguments();
                }
            }
        }

        return new Type[0];
    }

    /**
     * 打印类型的层次结构
     *
     * @param clazz 要分析的类
     */
    public static void printTypeHierarchy(Class<?> clazz) {
        System.out.println("类名: " + clazz.getName());
        System.out.println("父类: " + clazz.getSuperclass());
        System.out.println("实现的接口: " + Arrays.toString(clazz.getInterfaces()));

        Type genericSuperclass = clazz.getGenericSuperclass();
        System.out.println("泛型父类: " + genericSuperclass);

        Type[] genericInterfaces = clazz.getGenericInterfaces();
        System.out.println("泛型接口: " + Arrays.toString(genericInterfaces));
    }
}
