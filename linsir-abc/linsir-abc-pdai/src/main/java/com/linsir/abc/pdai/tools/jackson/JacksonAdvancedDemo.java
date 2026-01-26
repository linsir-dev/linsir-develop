package com.linsir.abc.pdai.tools.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Jackson 高级功能示例
 * 演示注解使用、日期格式化、空值处理等高级特性
 */
public class JacksonAdvancedDemo {

    /**
     * 演示 Jackson 高级功能
     */
    public static void demonstrateAdvancedFeatures() {
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 配置日期序列化
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());

            // 1. 演示注解使用
            System.out.println("=== 1. 演示注解使用 ===");
            AdvancedUser advancedUser = new AdvancedUser(
                    1, "John Doe", 30, "john@example.com", null, 
                    LocalDateTime.now(), new Date()
            );
            String jsonWithAnnotations = objectMapper.writeValueAsString(advancedUser);
            System.out.println("使用注解后的 JSON:");
            System.out.println(jsonWithAnnotations);

            // 2. 演示日期格式化
            System.out.println("\n=== 2. 演示日期格式化 ===");
            DateUser dateUser = new DateUser(1, "John Doe", LocalDateTime.now());
            String jsonWithDate = objectMapper.writeValueAsString(dateUser);
            System.out.println("格式化日期后的 JSON:");
            System.out.println(jsonWithDate);

            // 3. 演示空值处理
            System.out.println("\n=== 3. 演示空值处理 ===");
            NullableUser nullableUser = new NullableUser(1, "John Doe", null, null);
            String jsonWithNulls = objectMapper.writeValueAsString(nullableUser);
            System.out.println("包含空值的 JSON:");
            System.out.println(jsonWithNulls);

            // 4. 演示多态类型处理
            System.out.println("\n=== 4. 演示多态类型处理 ===");
            Animal dog = new Dog(1, "Buddy", "Golden Retriever");
            Animal cat = new Cat(2, "Whiskers", "Siamese");
            String jsonDog = objectMapper.writeValueAsString(dog);
            String jsonCat = objectMapper.writeValueAsString(cat);
            System.out.println("Dog JSON:");
            System.out.println(jsonDog);
            System.out.println("Cat JSON:");
            System.out.println(jsonCat);

        } catch (IOException e) {
            System.err.println("处理 JSON 时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 高级用户类，演示各种注解
     */
    @JsonInclude(JsonInclude.Include.NON_NULL) // 忽略空值
    static class AdvancedUser {
        @JsonProperty("user_id") // 自定义属性名
        private int id;

        @JsonProperty("full_name")
        private String name;

        @JsonIgnore // 忽略该属性
        private int age;

        @JsonProperty("email_address")
        private String email;

        @JsonProperty("phone_number")
        private String phone;

        @JsonProperty("registration_time")
        private LocalDateTime registrationTime;

        @JsonProperty("last_login")
        private Date lastLogin;

        // 无参构造函数
        public AdvancedUser() {
        }

        // 有参构造函数
        public AdvancedUser(int id, String name, int age, String email, String phone, 
                          LocalDateTime registrationTime, Date lastLogin) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
            this.phone = phone;
            this.registrationTime = registrationTime;
            this.lastLogin = lastLogin;
        }

        // getter 和 setter 方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public LocalDateTime getRegistrationTime() {
            return registrationTime;
        }

        public void setRegistrationTime(LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
        }

        public Date getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }
    }

    /**
     * 日期用户类，演示日期格式化
     */
    static class DateUser {
        private int id;
        private String name;

        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime registrationTime;

        // 无参构造函数
        public DateUser() {
        }

        // 有参构造函数
        public DateUser(int id, String name, LocalDateTime registrationTime) {
            this.id = id;
            this.name = name;
            this.registrationTime = registrationTime;
        }

        // getter 和 setter 方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getRegistrationTime() {
            return registrationTime;
        }

        public void setRegistrationTime(LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
        }
    }

    /**
     * 自定义 LocalDateTime 序列化器
     */
    static class CustomLocalDateTimeSerializer extends LocalDateTimeSerializer {
        public CustomLocalDateTimeSerializer() {
            super(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 可空用户类，演示空值处理
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 忽略空值和空字符串
    static class NullableUser {
        private int id;
        private String name;
        private String email;
        private String phone;

        // 无参构造函数
        public NullableUser() {
        }

        // 有参构造函数
        public NullableUser(int id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        // getter 和 setter 方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    /**
     * 动物基类，演示多态类型处理
     */
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Dog.class, name = "dog"),
            @JsonSubTypes.Type(value = Cat.class, name = "cat")
    })
    static abstract class Animal {
        private int id;
        private String name;

        // 无参构造函数
        public Animal() {
        }

        // 有参构造函数
        public Animal(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // getter 和 setter 方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 狗类，继承自动物类
     */
    static class Dog extends Animal {
        private String breed;

        // 无参构造函数
        public Dog() {
        }

        // 有参构造函数
        public Dog(int id, String name, String breed) {
            super(id, name);
            this.breed = breed;
        }

        // getter 和 setter 方法
        public String getBreed() {
            return breed;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }
    }

    /**
     * 猫类，继承自动物类
     */
    static class Cat extends Animal {
        private String color;

        // 无参构造函数
        public Cat() {
        }

        // 有参构造函数
        public Cat(int id, String name, String color) {
            super(id, name);
            this.color = color;
        }

        // getter 和 setter 方法
        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
