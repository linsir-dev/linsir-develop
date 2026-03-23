package com.linsir.spring.framework.spring_core.reflection.utils;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Spring 风格的类工具类
 * 提供类加载、类型判断、类名处理等功能
 */
public class ClassUtils {

    /**
     * 原始类型到包装类型的映射
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    /**
     * 包装类型到原始类型的映射
     */
    private static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap = new IdentityHashMap<>(8);

    static {
        // 初始化原始类型和包装类型的映射
        primitiveWrapperTypeMap.put(boolean.class, Boolean.class);
        primitiveWrapperTypeMap.put(byte.class, Byte.class);
        primitiveWrapperTypeMap.put(char.class, Character.class);
        primitiveWrapperTypeMap.put(double.class, Double.class);
        primitiveWrapperTypeMap.put(float.class, Float.class);
        primitiveWrapperTypeMap.put(int.class, Integer.class);
        primitiveWrapperTypeMap.put(long.class, Long.class);
        primitiveWrapperTypeMap.put(short.class, Short.class);

        // 反向映射
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            wrapperToPrimitiveTypeMap.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 获取默认类加载器
     * 优先返回当前线程的上下文类加载器
     *
     * @return 类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法获取线程上下文类加载器
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法获取系统类加载器
                }
            }
        }
        return cl;
    }

    /**
     * 使用类加载器加载类
     *
     * @param className   类全名
     * @param classLoader 类加载器
     * @return 类对象
     * @throws ClassNotFoundException 类未找到异常
     */
    public static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(className, true, classLoader);
    }

    /**
     * 使用默认类加载器加载类
     *
     * @param className 类全名
     * @return 类对象
     * @throws ClassNotFoundException 类未找到异常
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, getDefaultClassLoader());
    }

    // ==================== 类名处理 ====================

    /**
     * 获取短类名（不含包名）
     *
     * @param className 类全名
     * @return 短类名
     */
    public static String getShortName(String className) {
        int lastDotIndex = className.lastIndexOf('.');
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }

    /**
     * 获取短类名
     *
     * @param clazz 类对象
     * @return 短类名
     */
    public static String getShortName(Class<?> clazz) {
        return getShortName(clazz.getName());
    }

    /**
     * 获取类文件名称
     *
     * @param clazz 类对象
     * @return 类文件名称（如：UserService.class）
     */
    public static String getClassFileName(Class<?> clazz) {
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf('.');
        return className.substring(lastDotIndex + 1) + ".class";
    }

    /**
     * 将包名转换为资源路径
     *
     * @param clazz 类对象
     * @return 资源路径（如：com/example/service）
     */
    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null || clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName().replace('.', '/');
    }

    /**
     * 添加资源路径到包路径
     *
     * @param clazz        类对象
     * @param resourceName 资源名称
     * @return 完整资源路径
     */
    public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
        if (!resourceName.startsWith("/")) {
            return classPackageAsResourcePath(clazz) + "/" + resourceName;
        }
        return classPackageAsResourcePath(clazz) + resourceName;
    }

    /**
     * 获取合格类名（处理数组、原始类型）
     *
     * @param clazz 类对象
     * @return 合格类名
     */
    public static String getQualifiedName(Class<?> clazz) {
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        return clazz.getName();
    }

    /**
     * 获取数组类型的合格类名
     *
     * @param clazz 数组类对象
     * @return 合格类名
     */
    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            result.append("[]");
            clazz = clazz.getComponentType();
        }
        return clazz.getName() + result;
    }

    // ==================== 类型判断 ====================

    /**
     * 判断是否为原始类型
     *
     * @param clazz 类对象
     * @return true 表示是原始类型
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz != null && clazz.isPrimitive();
    }

    /**
     * 判断是否为原始类型包装类
     *
     * @param clazz 类对象
     * @return true 表示是包装类
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz != null && wrapperToPrimitiveTypeMap.containsKey(clazz);
    }

    /**
     * 判断是否为原始类型或包装类
     *
     * @param clazz 类对象
     * @return true 表示是原始类型或包装类
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz != null && (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 判断是否为数组类型
     *
     * @param clazz 类对象
     * @return true 表示是数组
     */
    public static boolean isArray(Class<?> clazz) {
        return clazz != null && clazz.isArray();
    }

    /**
     * 判断是否为原始类型数组
     *
     * @param clazz 类对象
     * @return true 表示是原始类型数组
     */
    public static boolean isPrimitiveArray(Class<?> clazz) {
        return clazz != null && clazz.isArray() && clazz.getComponentType().isPrimitive();
    }

    /**
     * 判断是否为内部类
     *
     * @param clazz 类对象
     * @return true 表示是内部类
     */
    public static boolean isInnerClass(Class<?> clazz) {
        return clazz != null && clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    /**
     * 判断是否为静态内部类
     *
     * @param clazz 类对象
     * @return true 表示是静态内部类
     */
    public static boolean isStaticInnerClass(Class<?> clazz) {
        return clazz != null && clazz.isMemberClass() && Modifier.isStatic(clazz.getModifiers());
    }

    /**
     * 判断是否为 CGLIB 代理类
     *
     * @param clazz 类对象
     * @return true 表示是 CGLIB 代理类
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

    /**
     * 判断是否为 JDK 动态代理类
     *
     * @param clazz 类对象
     * @return true 表示是 JDK 动态代理类
     */
    public static boolean isJdkDynamicProxy(Class<?> clazz) {
        return clazz != null && clazz.isInterface() == false && 
               java.lang.reflect.Proxy.isProxyClass(clazz);
    }

    // ==================== 类型转换 ====================

    /**
     * 解析原始类型为包装类型
     *
     * @param clazz 类对象
     * @return 如果是原始类型则返回包装类型，否则返回原类型
     */
    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        if (clazz != null && clazz.isPrimitive()) {
            return primitiveWrapperTypeMap.get(clazz);
        }
        return clazz;
    }

    /**
     * 解析类名为原始类型
     *
     * @param name 类名
     * @return 原始类型类对象，如果不是原始类型则返回 null
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        if (name == null || name.length() > 8) {
            return null;
        }
        return switch (name) {
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "char" -> char.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "short" -> short.class;
            default -> null;
        };
    }

    // ==================== 继承关系 ====================

    /**
     * 获取所有接口（包含父类接口）
     *
     * @param clazz 类对象
     * @return 接口列表
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Class<?>> interfaces = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();
        getAllInterfaces(clazz, interfaces, visited);
        return interfaces;
    }

    /**
     * 递归获取所有接口
     */
    private static void getAllInterfaces(Class<?> clazz, List<Class<?>> interfaces, Set<Class<?>> visited) {
        while (clazz != null) {
            for (Class<?> ifc : clazz.getInterfaces()) {
                if (visited.add(ifc)) {
                    interfaces.add(ifc);
                    getAllInterfaces(ifc, interfaces, visited);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 获取所有接口作为数组
     *
     * @param clazz 类对象
     * @return 接口类数组
     */
    public static Class<?>[] getAllInterfacesAsArray(Class<?> clazz) {
        return getAllInterfaces(clazz).toArray(new Class<?>[0]);
    }

    /**
     * 获取继承树
     *
     * @param clazz 类对象
     * @return 继承链列表（从当前类到 Object）
     */
    public static List<Class<?>> getInheritanceTree(Class<?> clazz) {
        List<Class<?>> inheritance = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            inheritance.add(clazz);
            clazz = clazz.getSuperclass();
        }
        if (clazz == Object.class) {
            inheritance.add(Object.class);
        }
        return inheritance;
    }

    /**
     * 判断类是否存在于类路径中
     *
     * @param className 类名
     * @param classLoader 类加载器
     * @return true 表示存在
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 判断类是否存在于类路径中（使用默认类加载器）
     *
     * @param className 类名
     * @return true 表示存在
     */
    public static boolean isPresent(String className) {
        return isPresent(className, getDefaultClassLoader());
    }
}
