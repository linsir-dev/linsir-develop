package com.linsir.abc.pdai.tools.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * GsonBuilder 配置示例
 * 演示 GsonBuilder 的各种配置选项
 */
public class GsonBuilderDemo {

    /**
     * 演示 GsonBuilder 配置
     */
    public static void demonstrateGsonBuilder() {
        // 1. 演示基本配置
        System.out.println("=== 1. 演示基本配置 ===");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting() // 美化输出
                .serializeNulls() // 序列化空值
                .disableHtmlEscaping() // 禁用 HTML 转义
                .create();

        TestUser testUser = new TestUser(1, "John Doe", null, new Date());
        String jsonBasic = gson.toJson(testUser);
        System.out.println("基本配置的 JSON:");
        System.out.println(jsonBasic);

        // 2. 演示日期格式化
        System.out.println("\n=== 2. 演示日期格式化 ===");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gsonWithDate = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat(dateFormat.toPattern())
                .create();

        String jsonWithDate = gsonWithDate.toJson(testUser);
        System.out.println("格式化日期的 JSON:");
        System.out.println(jsonWithDate);

        // 3. 演示自定义序列化器
        System.out.println("\n=== 3. 演示自定义序列化器 ===");
        Gson gsonWithCustomSerializer = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();

        UserWithLocalDateTime userWithLocalDateTime = new UserWithLocalDateTime(
                1, "John Doe", LocalDateTime.now()
        );
        String jsonWithCustomSerializer = gsonWithCustomSerializer.toJson(userWithLocalDateTime);
        System.out.println("使用自定义序列化器的 JSON:");
        System.out.println(jsonWithCustomSerializer);

        // 4. 演示版本控制
        System.out.println("\n=== 4. 演示版本控制 ===");
        Gson gsonV1 = new GsonBuilder()
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();

        Gson gsonV2 = new GsonBuilder()
                .setPrettyPrinting()
                .setVersion(2.0)
                .create();

        VersionedData versionedData = new VersionedData(
                "v1 data", "v2 data"
        );

        String jsonV1 = gsonV1.toJson(versionedData);
        System.out.println("版本 1.0 的 JSON:");
        System.out.println(jsonV1);

        String jsonV2 = gsonV2.toJson(versionedData);
        System.out.println("版本 2.0 的 JSON:");
        System.out.println(jsonV2);
    }

    /**
     * 测试用户类
     */
    public static class TestUser {
        private int id;
        private String name;
        private String email;
        private Date createdAt;

        // 无参构造函数
        public TestUser() {
        }

        // 有参构造函数
        public TestUser(int id, String name, String email, Date createdAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.createdAt = createdAt;
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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * 带 LocalDateTime 的用户类
     */
    public static class UserWithLocalDateTime {
        private int id;
        private String name;
        private LocalDateTime registrationTime;

        // 无参构造函数
        public UserWithLocalDateTime() {
        }

        // 有参构造函数
        public UserWithLocalDateTime(int id, String name, LocalDateTime registrationTime) {
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
     * 版本化数据类
     */
    public static class VersionedData {
        @com.google.gson.annotations.Since(1.0)
        private String v1Field;

        @com.google.gson.annotations.Since(2.0)
        private String v2Field;

        // 无参构造函数
        public VersionedData() {
        }

        // 有参构造函数
        public VersionedData(String v1Field, String v2Field) {
            this.v1Field = v1Field;
            this.v2Field = v2Field;
        }

        // getter 和 setter 方法
        public String getV1Field() {
            return v1Field;
        }

        public void setV1Field(String v1Field) {
            this.v1Field = v1Field;
        }

        public String getV2Field() {
            return v2Field;
        }

        public void setV2Field(String v2Field) {
            this.v2Field = v2Field;
        }
    }

    /**
     * 自定义 LocalDateTime 序列化器
     */
    public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }
    }
}
