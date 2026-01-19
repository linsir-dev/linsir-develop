package com.linsir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Person implements Serializable {
    @Id
    private int id;
    private String name;
    private int age;
}
