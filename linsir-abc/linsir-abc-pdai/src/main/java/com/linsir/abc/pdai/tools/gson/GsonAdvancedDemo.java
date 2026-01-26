package com.linsir.abc.pdai.tools.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gson 高级功能示例
 * 演示注解使用、日期格式化、空值处理等高级特性
 */
public class GsonAdvancedDemo {

    /**
     * 演示 Gson 高级功能
     */
    public static void demonstrateAdvancedFeatures() {
        // 创建 Gson 实例
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        // 1. 演示注解使用
        System.out.println("=== 1. 演示注解使用 ===");
        AdvancedUser advancedUser = new AdvancedUser(
                1, "John Doe", 30, "john@example.com", null
        );
        String jsonWithAnnotations = gson.toJson(advancedUser);
        System.out.println("使用注解后的 JSON:");
        System.out.println(jsonWithAnnotations);

        // 2. 演示版本控制
        System.out.println("\n=== 2. 演示版本控制 ===");
        VersionedUser versionedUser = new VersionedUser(
                1, "John Doe", "john@example.com", "john.doe@example.com"
        );
        
        // 版本 1.0
        Gson gsonV1 = new GsonBuilder()
                .setVersion(1.0)
                .create();
        String jsonV1 = gsonV1.toJson(versionedUser);
        System.out.println("版本 1.0 的 JSON:");
        System.out.println(jsonV1);
        
        // 版本 2.0
        Gson gsonV2 = new GsonBuilder()
                .setVersion(2.0)
                .create();
        String jsonV2 = gsonV2.toJson(versionedUser);
        System.out.println("版本 2.0 的 JSON:");
        System.out.println(jsonV2);

        // 3. 演示泛型处理
        System.out.println("\n=== 3. 演示泛型处理 ===");
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "John Doe", 30));
        userList.add(new User(2, "Jane Smith", 25));
        
        // 使用 TypeToken 处理泛型
        Type userListType = new TypeToken<List<User>>() {}.getType();
        String jsonList = gson.toJson(userList, userListType);
        System.out.println("泛型列表的 JSON:");
        System.out.println(jsonList);
        
        // 反序列化泛型
        List<User> parsedUserList = gson.fromJson(jsonList, userListType);
        System.out.println("解析后的泛型列表:");
        for (User u : parsedUserList) {
            System.out.println(u);
        }

        // 4. 演示嵌套对象
        System.out.println("\n=== 4. 演示嵌套对象 ===");
        Address address = new Address("123 Main St", "New York", "10001");
        UserWithAddress userWithAddress = new UserWithAddress(1, "John Doe", address);
        String jsonWithNested = gson.toJson(userWithAddress);
        System.out.println("包含嵌套对象的 JSON:");
        System.out.println(jsonWithNested);
        
        // 反序列化嵌套对象
        UserWithAddress parsedUserWithAddress = gson.fromJson(jsonWithNested, UserWithAddress.class);
        System.out.println("解析后的包含嵌套对象的用户:");
        System.out.println(parsedUserWithAddress);
    }

    /**
     * 高级用户类，演示各种注解
     */
    public static class AdvancedUser {
        @SerializedName("user_id") // 自定义属性名
        private int id;

        @SerializedName("full_name")
        private String name;

        @Expose(serialize = false, deserialize = false) // 不序列化也不反序列化
        private int age;

        @SerializedName("email_address")
        private String email;

        @SerializedName("phone_number")
        private String phone;

        // 无参构造函数
        public AdvancedUser() {
        }

        // 有参构造函数
        public AdvancedUser(int id, String name, int age, String email, String phone) {
            this.id = id;
            this.name = name;
            this.age = age;
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
    }

    /**
     * 版本控制用户类
     */
    public static class VersionedUser {
        private int id;
        private String name;
        
        @Since(1.0)
        private String email;
        
        @Since(2.0)
        private String secondaryEmail;

        // 无参构造函数
        public VersionedUser() {
        }

        // 有参构造函数
        public VersionedUser(int id, String name, String email, String secondaryEmail) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.secondaryEmail = secondaryEmail;
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

        public String getSecondaryEmail() {
            return secondaryEmail;
        }

        public void setSecondaryEmail(String secondaryEmail) {
            this.secondaryEmail = secondaryEmail;
        }
    }

    /**
     * 简单用户类
     */
    public static class User {
        private int id;
        private String name;
        private int age;

        // 无参构造函数
        public User() {
        }

        // 有参构造函数
        public User(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
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

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    /**
     * 地址类
     */
    public static class Address {
        private String street;
        private String city;
        private String zip;

        // 无参构造函数
        public Address() {
        }

        // 有参构造函数
        public Address(String street, String city, String zip) {
            this.street = street;
            this.city = city;
            this.zip = zip;
        }

        // getter 和 setter 方法
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", city='" + city + '\'' +
                    ", zip='" + zip + '\'' +
                    '}';
        }
    }

    /**
     * 带地址的用户类
     */
    public static class UserWithAddress {
        private int id;
        private String name;
        private Address address;

        // 无参构造函数
        public UserWithAddress() {
        }

        // 有参构造函数
        public UserWithAddress(int id, String name, Address address) {
            this.id = id;
            this.name = name;
            this.address = address;
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

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "UserWithAddress{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address=" + address +
                    '}';
        }
    }
}
