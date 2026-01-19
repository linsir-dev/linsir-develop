package com.linsir.spring.framework.mock;

import lombok.Data;

/**
 * @ClassName : User
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-06 14:22
 */

@Data
public class User {

    public User(int id,String name, int age)
    {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    private int id;

    private String name;

    private int age;

}
