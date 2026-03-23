package com.linsir.spring.framework.spring_core.conversion.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * 类型描述符
 * 用于描述类型的完整信息，包括泛型参数
 *
 * <p>该类提供了对 Java 类型的完整描述，包括：</p>
 * <ul>
 *   <li>原始类型</li>
 *   <li>泛型参数</li>
 *   <li>注解信息</li>
 *   <li>集合元素类型</li>
 *   <li>Map 的键值类型</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class TypeDescriptor {

    private final Class<?> type;
    private final Type genericType;
    private final Annotation[] annotations;

    /**
     * 构造类型描述符
     *
     * @param type 类型
     */
    public TypeDescriptor(Class<?> type) {
        this(type, type, null);
    }

    /**
     * 构造类型描述符
     *
     * @param type 类型
     * @param genericType 泛型类型
     */
    public TypeDescriptor(Class<?> type, Type genericType) {
        this(type, genericType, null);
    }

    /**
     * 构造类型描述符
     *
     * @param type 类型
     * @param genericType 泛型类型
     * @param annotations 注解数组
     */
    public TypeDescriptor(Class<?> type, Type genericType, Annotation[] annotations) {
        this.type = type;
        this.genericType = genericType;
        this.annotations = annotations != null ? annotations : new Annotation[0];
    }

    /**
     * 从字段创建类型描述符
     *
     * @param field 字段
     * @return 类型描述符
     */
    public static TypeDescriptor forField(Field field) {
        return new TypeDescriptor(field.getType(), field.getGenericType(), field.getAnnotations());
    }

    /**
     * 从方法参数创建类型描述符
     *
     * @param method 方法
     * @param parameterIndex 参数索引
     * @return 类型描述符
     */
    public static TypeDescriptor forMethodParameter(Method method, int parameterIndex) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Type[] genericParamTypes = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        if (parameterIndex < 0 || parameterIndex >= paramTypes.length) {
            throw new IllegalArgumentException("Invalid parameter index: " + parameterIndex);
        }

        return new TypeDescriptor(
                paramTypes[parameterIndex],
                genericParamTypes[parameterIndex],
                paramAnnotations[parameterIndex]
        );
    }

    /**
     * 从对象创建类型描述符
     *
     * @param object 对象
     * @return 类型描述符
     */
    public static TypeDescriptor forObject(Object object) {
        if (object == null) {
            return new TypeDescriptor(Object.class);
        }
        return new TypeDescriptor(object.getClass());
    }

    /**
     * 创建集合类型描述符
     *
     * @param collectionType 集合类型
     * @param elementType 元素类型描述符
     * @return 集合类型描述符
     */
    public static TypeDescriptor collection(Class<? extends Collection> collectionType, TypeDescriptor elementType) {
        return new TypeDescriptor(collectionType);
    }

    /**
     * 创建 Map 类型描述符
     *
     * @param mapType Map 类型
     * @param keyType 键类型描述符
     * @param valueType 值类型描述符
     * @return Map 类型描述符
     */
    public static TypeDescriptor map(Class<? extends Map> mapType, TypeDescriptor keyType, TypeDescriptor valueType) {
        return new TypeDescriptor(mapType);
    }

    /**
     * 创建值类型描述符
     *
     * @param type 类型
     * @return 类型描述符
     */
    public static TypeDescriptor valueOf(Class<?> type) {
        return new TypeDescriptor(type);
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * 获取对象类型（与 getType 相同，用于兼容）
     *
     * @return 对象类型
     */
    public Class<?> getObjectType() {
        return type;
    }

    /**
     * 获取泛型类型
     *
     * @return 泛型类型
     */
    public Type getGenericType() {
        return genericType;
    }

    /**
     * 获取注解数组
     *
     * @return 注解数组
     */
    public Annotation[] getAnnotations() {
        return annotations.clone();
    }

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型泛型
     * @return 注解实例，如果不存在返回 null
     */
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }

    /**
     * 判断是否为集合类型
     *
     * @return 如果是集合类型返回 true
     */
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(type);
    }

    /**
     * 判断是否为 Map 类型
     *
     * @return 如果是 Map 类型返回 true
     */
    public boolean isMap() {
        return Map.class.isAssignableFrom(type);
    }

    /**
     * 判断是否为数组类型
     *
     * @return 如果是数组类型返回 true
     */
    public boolean isArray() {
        return type.isArray();
    }

    /**
     * 获取集合元素类型
     *
     * @return 元素类型描述符，如果不是集合返回 null
     */
    public TypeDescriptor getElementTypeDescriptor() {
        if (!isCollection() && !isArray()) {
            return null;
        }

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = paramType.getActualTypeArguments();
            if (actualTypeArgs.length > 0 && actualTypeArgs[0] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypeArgs[0]);
            }
        }

        // 对于数组类型
        if (isArray()) {
            return new TypeDescriptor(type.getComponentType());
        }

        return null;
    }

    /**
     * 获取 Map 的键类型
     *
     * @return 键类型描述符，如果不是 Map 返回 null
     */
    public TypeDescriptor getMapKeyTypeDescriptor() {
        if (!isMap()) {
            return null;
        }

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = paramType.getActualTypeArguments();
            if (actualTypeArgs.length > 0 && actualTypeArgs[0] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypeArgs[0]);
            }
        }

        return null;
    }

    /**
     * 获取 Map 的值类型
     *
     * @return 值类型描述符，如果不是 Map 返回 null
     */
    public TypeDescriptor getMapValueTypeDescriptor() {
        if (!isMap()) {
            return null;
        }

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = paramType.getActualTypeArguments();
            if (actualTypeArgs.length > 1 && actualTypeArgs[1] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypeArgs[1]);
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TypeDescriptor other = (TypeDescriptor) obj;
        return type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "TypeDescriptor [type=" + type.getName() + "]";
    }
}
