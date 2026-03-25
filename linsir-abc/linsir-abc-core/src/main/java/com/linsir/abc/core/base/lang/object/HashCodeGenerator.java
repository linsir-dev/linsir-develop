package com.linsir.abc.core.base.lang.object;

import java.util.Objects;

/**
 * 哈希码生成器
 * 
 * 本类提供多种哈希码生成策略，演示不同场景下的最佳实践：
 * 1. 单字段对象的哈希码生成
 * 2. 多字段对象的哈希码生成
 * 3. 数组字段的哈希码生成
 * 4. 高性能哈希码生成
 * 
 * 哈希码设计原则：
 * - 一致性：同一对象多次调用返回相同值
 * - 相等性：equals相等的对象hashCode必须相等
 * - 分布性：hashCode应尽可能均匀分布以减少冲突
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class HashCodeGenerator {
    
    /**
     * 初始哈希值
     * 使用非零初始值可以减少哈希冲突
     */
    private static final int INITIAL_HASH = 17;
    
    /**
     * 乘数因子
     * 使用质数作为乘数可以获得更好的哈希分布
     */
    private static final int MULTIPLIER = 31;
    
    /**
     * 私有构造方法，防止实例化
     */
    private HashCodeGenerator() {
        throw new AssertionError("工具类不应被实例化");
    }
    
    /**
     * 为单字段对象生成哈希码
     * 
     * @param field 对象字段
     * @return 哈希码
     */
    public static int generateForSingleField(Object field) {
        return Objects.hashCode(field);
    }
    
    /**
     * 为多字段对象生成哈希码（推荐方式）
     * 
     * 使用Objects.hash方法，简洁且安全
     * 
     * @param fields 对象的所有字段
     * @return 哈希码
     */
    public static int generateForMultipleFields(Object... fields) {
        return Objects.hash(fields);
    }
    
    /**
     * 手动计算多字段哈希码（高性能方式）
     * 
     * 这种方式比Objects.hash性能更好，因为避免了数组创建
     * 公式：result = 31 * result + fieldHash
     * 
     * @param fields 对象的所有字段
     * @return 哈希码
     */
    public static int generateManually(Object... fields) {
        int result = INITIAL_HASH;
        
        for (Object field : fields) {
            result = MULTIPLIER * result + (field == null ? 0 : field.hashCode());
        }
        
        return result;
    }
    
    /**
     * 为包含数组字段的对象生成哈希码
     * 
     * 使用Arrays.hashCode处理数组字段
     * 
     * @param intArray 整型数组
     * @param objectArray 对象数组
     * @param otherField 其他字段
     * @return 哈希码
     */
    public static int generateWithArrays(int[] intArray, Object[] objectArray, Object otherField) {
        int result = INITIAL_HASH;
        
        // 处理基本类型数组
        result = MULTIPLIER * result + java.util.Arrays.hashCode(intArray);
        
        // 处理对象数组
        result = MULTIPLIER * result + java.util.Arrays.hashCode(objectArray);
        
        // 处理其他字段
        result = MULTIPLIER * result + (otherField == null ? 0 : otherField.hashCode());
        
        return result;
    }
    
    /**
     * 为布尔值生成哈希码
     * 
     * @param value 布尔值
     * @return 哈希码
     */
    public static int hashCodeForBoolean(boolean value) {
        return value ? 1231 : 1237;
    }
    
    /**
     * 为字节生成哈希码
     * 
     * @param value 字节值
     * @return 哈希码
     */
    public static int hashCodeForByte(byte value) {
        return (int) value;
    }
    
    /**
     * 为字符生成哈希码
     * 
     * @param value 字符值
     * @return 哈希码
     */
    public static int hashCodeForChar(char value) {
        return (int) value;
    }
    
    /**
     * 为短整型生成哈希码
     * 
     * @param value 短整型值
     * @return 哈希码
     */
    public static int hashCodeForShort(short value) {
        return (int) value;
    }
    
    /**
     * 为整型生成哈希码
     * 
     * @param value 整型值
     * @return 哈希码
     */
    public static int hashCodeForInt(int value) {
        return value;
    }
    
    /**
     * 为长整型生成哈希码
     * 
     * 使用高位和低位异或，使所有位都参与哈希计算
     * 
     * @param value 长整型值
     * @return 哈希码
     */
    public static int hashCodeForLong(long value) {
        return (int) (value ^ (value >>> 32));
    }
    
    /**
     * 为浮点型生成哈希码
     * 
     * @param value 浮点型值
     * @return 哈希码
     */
    public static int hashCodeForFloat(float value) {
        return Float.floatToIntBits(value);
    }
    
    /**
     * 为双精度浮点型生成哈希码
     * 
     * @param value 双精度浮点型值
     * @return 哈希码
     */
    public static int hashCodeForDouble(double value) {
        long bits = Double.doubleToLongBits(value);
        return hashCodeForLong(bits);
    }
    
    /**
     * 演示类：展示如何使用哈希码生成器
     */
    public static class Person {
        
        private final Long id;
        private final String name;
        private final int age;
        private final String[] hobbies;
        
        public Person(Long id, String name, int age, String[] hobbies) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.hobbies = hobbies;
        }
        
        /**
         * 方式1：使用Objects.hash（简洁但性能稍差）
         */
        public int hashCodeSimple() {
            return Objects.hash(id, name, age, hobbies);
        }
        
        /**
         * 方式2：手动计算（性能更好）
         */
        @Override
        public int hashCode() {
            int result = INITIAL_HASH;
            result = MULTIPLIER * result + (id == null ? 0 : id.hashCode());
            result = MULTIPLIER * result + (name == null ? 0 : name.hashCode());
            result = MULTIPLIER * result + age;
            result = MULTIPLIER * result + java.util.Arrays.hashCode(hobbies);
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            Person other = (Person) obj;
            return age == other.age &&
                   Objects.equals(id, other.id) &&
                   Objects.equals(name, other.name) &&
                   java.util.Arrays.equals(hobbies, other.hobbies);
        }
    }
}
