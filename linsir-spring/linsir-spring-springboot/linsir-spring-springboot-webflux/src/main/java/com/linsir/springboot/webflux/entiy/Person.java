package com.linsir.springboot.webflux.entiy;

import lombok.Data;

/**
 * @ClassName : Person
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-16 08:01
 */

@Data
public class Person {

    private Long id;
    private String name;
    private int age;

    public Person(Long id,String name,int age)
    {
        this.id = id;
        this.name = name;
        this.age = age;
    }


}
