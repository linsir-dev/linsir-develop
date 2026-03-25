package com.linsir.abc.core.base.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射检查器
 * 
 * 本类演示Java反射API的使用：
 * 1. 获取Class对象的多种方式
 * 2. 获取类的构造方法、字段和方法
 * 3. 调用方法和访问字段
 * 4. 反射的注意事项和性能影响
 * 
 * 反射的应用场景：
 * - 框架开发（Spring、MyBatis等）
 * - 序列化和反序列化
 * - 动态代理
 * - 单元测试
 * 
 * 反射的性能开销：
 * - 绕过编译时类型检查
 * - 需要额外的安全检查
 * - 建议缓存反射获取的Method、Field对象
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ReflectionInspector {
    
    /**
     * 获取Class对象的三种方式
     * 
     * @param className 类全名
     * @return Class对象
     * @throws ClassNotFoundException 如果类不存在
     */
    public Class<?> getClassByName(String className) throws ClassNotFoundException {
        // 方式1：通过类名获取（需要处理ClassNotFoundException）
        return Class.forName(className);
    }
    
    /**
     * 通过对象获取Class
     * 
     * @param obj 对象
     * @return Class对象
     */
    public Class<?> getClassByObject(Object obj) {
        // 方式2：通过对象获取
        return obj.getClass();
    }
    
    /**
     * 通过类字面量获取Class
     * 
     * @param clazz 类
     * @return Class对象
     */
    public <T> Class<T> getClassByLiteral(Class<T> clazz) {
        // 方式3：通过类字面量获取（编译时检查，推荐）
        return clazz;
    }
    
    /**
     * 检查类的基本信息
     * 
     * @param clazz 目标类
     * @return 类信息描述
     */
    public String inspectClassBasicInfo(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("类名: ").append(clazz.getName()).append("\n");
        sb.append("简单类名: ").append(clazz.getSimpleName()).append("\n");
        sb.append("包名: ").append(clazz.getPackage() != null ? clazz.getPackage().getName() : "默认包").append("\n");
        sb.append("修饰符: ").append(Modifier.toString(clazz.getModifiers())).append("\n");
        sb.append("是否为接口: ").append(clazz.isInterface()).append("\n");
        sb.append("是否为数组: ").append(clazz.isArray()).append("\n");
        sb.append("是否为基本类型: ").append(clazz.isPrimitive()).append("\n");
        sb.append("是否为枚举: ").append(clazz.isEnum()).append("\n");
        sb.append("是否为注解: ").append(clazz.isAnnotation()).append("\n");
        
        // 继承关系
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            sb.append("父类: ").append(superclass.getName()).append("\n");
        }
        
        // 实现的接口
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            sb.append("实现的接口: ");
            for (int i = 0; i < interfaces.length; i++) {
                sb.append(interfaces[i].getName());
                if (i < interfaces.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 获取类的所有构造方法
     * 
     * @param clazz 目标类
     * @return 构造方法信息列表
     */
    public List<String> getConstructorsInfo(Class<?> clazz) {
        List<String> constructorsInfo = new ArrayList<>();
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        
        for (Constructor<?> constructor : constructors) {
            StringBuilder sb = new StringBuilder();
            sb.append(Modifier.toString(constructor.getModifiers())).append(" ");
            sb.append(constructor.getName()).append("(");
            
            Class<?>[] params = constructor.getParameterTypes();
            for (int i = 0; i < params.length; i++) {
                sb.append(params[i].getSimpleName());
                if (i < params.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            constructorsInfo.add(sb.toString());
        }
        
        return constructorsInfo;
    }
    
    /**
     * 获取类的所有字段
     * 
     * @param clazz 目标类
     * @return 字段信息列表
     */
    public List<String> getFieldsInfo(Class<?> clazz) {
        List<String> fieldsInfo = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            StringBuilder sb = new StringBuilder();
            sb.append(Modifier.toString(field.getModifiers())).append(" ");
            sb.append(field.getType().getSimpleName()).append(" ");
            sb.append(field.getName());
            fieldsInfo.add(sb.toString());
        }
        
        return fieldsInfo;
    }
    
    /**
     * 获取类的所有方法
     * 
     * @param clazz 目标类
     * @return 方法信息列表
     */
    public List<String> getMethodsInfo(Class<?> clazz) {
        List<String> methodsInfo = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        
        for (Method method : methods) {
            StringBuilder sb = new StringBuilder();
            sb.append(Modifier.toString(method.getModifiers())).append(" ");
            sb.append(method.getReturnType().getSimpleName()).append(" ");
            sb.append(method.getName()).append("(");
            
            Class<?>[] params = method.getParameterTypes();
            for (int i = 0; i < params.length; i++) {
                sb.append(params[i].getSimpleName());
                if (i < params.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            methodsInfo.add(sb.toString());
        }
        
        return methodsInfo;
    }
    
    /**
     * 通过反射创建对象实例
     * 
     * @param clazz 目标类
     * @param args 构造参数
     * @return 创建的实例
     * @throws Exception 如果创建失败
     */
    public <T> T createInstance(Class<T> clazz, Object... args) throws Exception {
        // 获取参数类型
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        
        // 获取构造方法
        Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
        
        // 设置可访问（用于私有构造方法）
        constructor.setAccessible(true);
        
        // 创建实例
        return constructor.newInstance(args);
    }
    
    /**
     * 通过反射调用方法
     * 
     * @param target 目标对象
     * @param methodName 方法名
     * @param args 方法参数
     * @return 方法返回值
     * @throws Exception 如果调用失败
     */
    public Object invokeMethod(Object target, String methodName, Object... args) throws Exception {
        Class<?> clazz = target.getClass();
        
        // 获取参数类型
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        
        // 获取方法 - 支持基本类型匹配
        Method method = findMethod(clazz, methodName, argTypes);
        if (method == null) {
            throw new NoSuchMethodException("方法不存在: " + methodName);
        }
        
        // 设置可访问（用于私有方法）
        method.setAccessible(true);
        
        // 调用方法
        return method.invoke(target, args);
    }
    
    /**
     * 查找方法，支持基本类型和包装类型的匹配
     */
    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != argTypes.length) {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < paramTypes.length; i++) {
                if (!isAssignable(paramTypes[i], argTypes[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * 检查类型是否可赋值
     */
    private boolean isAssignable(Class<?> paramType, Class<?> argType) {
        if (paramType.equals(argType)) {
            return true;
        }
        // 基本类型和包装类型的匹配
        if (paramType.isPrimitive()) {
            if (paramType == int.class && argType == Integer.class) return true;
            if (paramType == long.class && argType == Long.class) return true;
            if (paramType == short.class && argType == Short.class) return true;
            if (paramType == byte.class && argType == Byte.class) return true;
            if (paramType == double.class && argType == Double.class) return true;
            if (paramType == float.class && argType == Float.class) return true;
            if (paramType == boolean.class && argType == Boolean.class) return true;
            if (paramType == char.class && argType == Character.class) return true;
        }
        return paramType.isAssignableFrom(argType);
    }
    
    /**
     * 通过反射获取字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名
     * @return 字段值
     * @throws Exception 如果获取失败
     */
    public Object getFieldValue(Object target, String fieldName) throws Exception {
        Class<?> clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
    
    /**
     * 通过反射设置字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名
     * @param value 字段值
     * @throws Exception 如果设置失败
     */
    public void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Class<?> clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    
    /**
     * 获取类的所有注解
     * 
     * @param clazz 目标类
     * @return 注解信息列表
     */
    public List<String> getAnnotationsInfo(Class<?> clazz) {
        List<String> annotationsInfo = new ArrayList<>();
        java.lang.annotation.Annotation[] annotations = clazz.getDeclaredAnnotations();
        
        for (java.lang.annotation.Annotation annotation : annotations) {
            annotationsInfo.add(annotation.annotationType().getName());
        }
        
        return annotationsInfo;
    }
    
    /**
     * 打印类的完整反射信息
     * 
     * @param clazz 目标类
     */
    public void printFullInspection(Class<?> clazz) {
        System.out.println("========== 类基本信息 ==========");
        System.out.println(inspectClassBasicInfo(clazz));
        
        System.out.println("========== 构造方法 ==========");
        for (String constructor : getConstructorsInfo(clazz)) {
            System.out.println(constructor);
        }
        
        System.out.println("\n========== 字段 ==========");
        for (String field : getFieldsInfo(clazz)) {
            System.out.println(field);
        }
        
        System.out.println("\n========== 方法 ==========");
        for (String method : getMethodsInfo(clazz)) {
            System.out.println(method);
        }
        
        System.out.println("\n========== 注解 ==========");
        List<String> annotations = getAnnotationsInfo(clazz);
        if (annotations.isEmpty()) {
            System.out.println("无注解");
        } else {
            for (String annotation : annotations) {
                System.out.println(annotation);
            }
        }
    }
    
    /**
     * 演示反射的基本使用
     */
    public void demonstrateReflection() {
        try {
            // 1. 获取Class对象
            Class<?> clazz = String.class;
            System.out.println("检查类: " + clazz.getName());
            
            // 2. 获取构造方法并创建实例
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object instance = constructor.newInstance("Hello Reflection");
            System.out.println("创建实例: " + instance);
            
            // 3. 获取方法并调用
            Method lengthMethod = clazz.getMethod("length");
            Object result = lengthMethod.invoke(instance);
            System.out.println("调用length方法: " + result);
            
            // 4. 获取字段
            Field valueField = clazz.getDeclaredField("value");
            valueField.setAccessible(true);
            System.out.println("value字段类型: " + valueField.getType().getName());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试用的示例类
     */
    public static class TestClass {
        private String privateField;
        public int publicField;
        
        public TestClass() {
            this.privateField = "default";
        }
        
        public TestClass(String value) {
            this.privateField = value;
        }
        
        private TestClass(int value) {
            this.publicField = value;
        }
        
        public String getPrivateField() {
            return privateField;
        }
        
        public void setPrivateField(String value) {
            this.privateField = value;
        }
        
        private void privateMethod() {
            System.out.println("私有方法被调用");
        }
        
        public int add(int a, int b) {
            return a + b;
        }
    }
}
