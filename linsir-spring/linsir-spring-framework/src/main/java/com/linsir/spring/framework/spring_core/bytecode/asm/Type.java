package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM类型描述符
 *
 * <p>提供了Java类型与JVM类型描述符之间的转换。
 * JVM使用特定的字符串格式来描述类型，例如：
 * <ul>
 *   <li>I - int类型</li>
 *   <li>Ljava/lang/String; - String类型</li>
 *   <li>[I - int数组</li>
 *   <li>(II)I - 接受两个int参数，返回int的方法</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 获取int类型的描述符
 * String intDesc = Type.INT_TYPE.getDescriptor(); // "I"
 *
 * // 获取String类型的描述符
 * String stringDesc = Type.getDescriptor(String.class); // "Ljava/lang/String;"
 *
 * // 获取方法描述符
 * String methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
 *     Type.INT_TYPE, Type.getType(String.class)); // "(ILjava/lang/String;)V"
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see Opcodes
 */
public class Type {

    /**
     * 类型排序常量
     */
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final int METHOD = 11;

    /**
     * 基本类型实例
     */
    public static final Type VOID_TYPE = new Type(VOID, null, 'V', 1, 1);
    public static final Type BOOLEAN_TYPE = new Type(BOOLEAN, null, 'Z', 1, 1);
    public static final Type CHAR_TYPE = new Type(CHAR, null, 'C', 1, 1);
    public static final Type BYTE_TYPE = new Type(BYTE, null, 'B', 1, 1);
    public static final Type SHORT_TYPE = new Type(SHORT, null, 'S', 1, 1);
    public static final Type INT_TYPE = new Type(INT, null, 'I', 1, 1);
    public static final Type FLOAT_TYPE = new Type(FLOAT, null, 'F', 1, 1);
    public static final Type LONG_TYPE = new Type(LONG, null, 'J', 1, 2);
    public static final Type DOUBLE_TYPE = new Type(DOUBLE, null, 'D', 1, 2);

    /**
     * 类型排序
     */
    private final int sort;

    /**
     * 类型描述符
     */
    private final String descriptor;

    /**
     * 基本类型字符
     */
    private final char primitiveChar;

    /**
     * 类型大小（用于计算局部变量表和操作数栈）
     */
    private final int size;

    /**
     * 在栈中占用的槽位数（long和double占2个）
     */
    private final int slots;

    /**
     * 私有构造函数
     */
    private Type(int sort, String descriptor, char primitiveChar, int size, int slots) {
        this.sort = sort;
        this.descriptor = descriptor;
        this.primitiveChar = primitiveChar;
        this.size = size;
        this.slots = slots;
    }

    /**
     * 从Class对象创建Type
     *
     * @param clazz Class对象
     * @return Type实例
     */
    public static Type getType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == void.class) return VOID_TYPE;
            if (clazz == boolean.class) return BOOLEAN_TYPE;
            if (clazz == char.class) return CHAR_TYPE;
            if (clazz == byte.class) return BYTE_TYPE;
            if (clazz == short.class) return SHORT_TYPE;
            if (clazz == int.class) return INT_TYPE;
            if (clazz == float.class) return FLOAT_TYPE;
            if (clazz == long.class) return LONG_TYPE;
            if (clazz == double.class) return DOUBLE_TYPE;
        } else if (clazz.isArray()) {
            return new Type(ARRAY, getDescriptor(clazz), '\0', 1, 1);
        }
        return new Type(OBJECT, getDescriptor(clazz), '\0', 1, 1);
    }

    /**
     * 从描述符创建Type
     *
     * @param descriptor 类型描述符
     * @return Type实例
     */
    public static Type getType(String descriptor) {
        return getType(descriptor, 0);
    }

    /**
     * 从描述符的指定位置创建Type
     *
     * @param descriptor 类型描述符
     * @param start 起始位置
     * @return Type实例
     */
    private static Type getType(String descriptor, int start) {
        char c = descriptor.charAt(start);
        switch (c) {
            case 'V': return VOID_TYPE;
            case 'Z': return BOOLEAN_TYPE;
            case 'C': return CHAR_TYPE;
            case 'B': return BYTE_TYPE;
            case 'S': return SHORT_TYPE;
            case 'I': return INT_TYPE;
            case 'F': return FLOAT_TYPE;
            case 'J': return LONG_TYPE;
            case 'D': return DOUBLE_TYPE;
            case '[':
                return new Type(ARRAY, descriptor.substring(start), '\0', 1, 1);
            case 'L':
                int end = descriptor.indexOf(';', start);
                return new Type(OBJECT, descriptor.substring(start, end + 1), '\0', 1, 1);
            case '(':
                return new Type(METHOD, descriptor.substring(start), '\0', 1, 1);
            default:
                throw new IllegalArgumentException("无效的类型描述符: " + descriptor);
        }
    }

    /**
     * 获取类型的内部名称
     *
     * <p>内部名称是类全限定名中的点替换为斜杠，例如：
     * java.lang.String -> java/lang/String
     *
     * @param clazz Class对象
     * @return 内部名称
     */
    public static String getInternalName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    /**
     * 获取类型的描述符
     *
     * @param clazz Class对象
     * @return 类型描述符
     */
    public static String getDescriptor(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == void.class) return "V";
            if (clazz == boolean.class) return "Z";
            if (clazz == char.class) return "C";
            if (clazz == byte.class) return "B";
            if (clazz == short.class) return "S";
            if (clazz == int.class) return "I";
            if (clazz == float.class) return "F";
            if (clazz == long.class) return "J";
            if (clazz == double.class) return "D";
        } else if (clazz.isArray()) {
            return "[" + getDescriptor(clazz.getComponentType());
        } else {
            return "L" + getInternalName(clazz) + ";";
        }
        throw new IllegalArgumentException("未知的类型: " + clazz);
    }

    /**
     * 获取方法描述符
     *
     * @param returnType 返回类型
     * @param argumentTypes 参数类型数组
     * @return 方法描述符
     */
    public static String getMethodDescriptor(Type returnType, Type... argumentTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type argType : argumentTypes) {
            sb.append(argType.getDescriptor());
        }
        sb.append(')');
        sb.append(returnType.getDescriptor());
        return sb.toString();
    }

    /**
     * 获取类型排序
     *
     * @return 类型排序常量
     */
    public int getSort() {
        return sort;
    }

    /**
     * 获取类型描述符
     *
     * @return 类型描述符字符串
     */
    public String getDescriptor() {
        if (descriptor != null) {
            return descriptor;
        }
        return String.valueOf(primitiveChar);
    }

    /**
     * 获取类型大小
     *
     * @return 类型大小
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取栈槽位数
     *
     * @return 栈槽位数
     */
    public int getSlots() {
        return slots;
    }

    /**
     * 是否是基本类型
     *
     * @return true如果是基本类型
     */
    public boolean isPrimitive() {
        return sort >= VOID && sort <= DOUBLE;
    }

    /**
     * 是否是数组类型
     *
     * @return true如果是数组类型
     */
    public boolean isArray() {
        return sort == ARRAY;
    }

    /**
     * 是否是对象类型
     *
     * @return true如果是对象类型
     */
    public boolean isObject() {
        return sort == OBJECT;
    }

    /**
     * 是否是方法类型
     *
     * @return true如果是方法类型
     */
    public boolean isMethod() {
        return sort == METHOD;
    }

    @Override
    public String toString() {
        return getDescriptor();
    }
}
