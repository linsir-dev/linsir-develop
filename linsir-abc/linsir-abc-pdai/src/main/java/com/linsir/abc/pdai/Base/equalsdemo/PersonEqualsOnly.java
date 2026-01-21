package com.linsir.abc.pdai.base.equalsdemo;

import java.util.Objects;

/**
 * 仅重写 equals()，没有重写 hashCode()
 */
public class PersonEqualsOnly {
    private String name;
    private int age;

    public PersonEqualsOnly(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 只重写 equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEqualsOnly)) return false;
        PersonEqualsOnly that = (PersonEqualsOnly) o;
        return age == that.age && Objects.equals(name, that.name);
    }

    // 注意：没有重写 hashCode()
    @Override
    public String toString() {
        return "PersonEqualsOnly{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}