package com.linsir.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("person")
public class Person {

    @Id
    private int id;

    private String name;

    private int age;

    private String  country,city;




    /*public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Person() {

    }

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
    }*/
}
