package com.linsir.abc.pdai.base.annotationdemo;

/**
 * 使用自定义注解的示例类
 * 演示如何在类、方法和字段上应用自定义注解
 */
@LinAnnotation(value = "Annotation Demo", name = "AnnotationDemo", version = 1)
public class AnnotationDemo {

    @LinFieldAnnotation(value = "id", required = true, description = "用户ID")
    private int id;

    @LinFieldAnnotation(value = "name", required = true, description = "用户名")
    private String name;

    @LinFieldAnnotation(value = "age", required = false, description = "用户年龄")
    private int age;

    /**
     * 构造方法
     */
    public AnnotationDemo(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    /**
     * 获取ID方法
     */
    @LinMethodAnnotation(value = "get id", author = "linsir", date = "2026-01-21", tags = {"getter", "id"})
    public int getId() {
        return id;
    }

    /**
     * 设置ID方法
     */
    @LinMethodAnnotation(value = "set id", author = "linsir", date = "2026-01-21", tags = {"setter", "id"})
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取名称方法
     */
    @LinMethodAnnotation(value = "get name", author = "linsir", date = "2026-01-21", tags = {"getter", "name"})
    public String getName() {
        return name;
    }

    /**
     * 设置名称方法
     */
    @LinMethodAnnotation(value = "set name", author = "linsir", date = "2026-01-21", tags = {"setter", "name"})
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取年龄方法
     */
    @LinMethodAnnotation(value = "get age", author = "linsir", date = "2026-01-21", tags = {"getter", "age"})
    public int getAge() {
        return age;
    }

    /**
     * 设置年龄方法
     */
    @LinMethodAnnotation(value = "set age", author = "linsir", date = "2026-01-21", tags = {"setter", "age"})
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * 显示信息方法
     */
    @LinMethodAnnotation(value = "show info", author = "linsir", date = "2026-01-21", tags = {"display", "info"})
    public void showInfo() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
    }
}
