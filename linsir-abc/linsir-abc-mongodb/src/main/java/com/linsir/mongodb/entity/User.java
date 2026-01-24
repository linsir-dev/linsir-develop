package com.linsir.mongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}