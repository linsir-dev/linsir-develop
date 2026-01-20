package com.linsir.abc.pdai.Base.obj;

/**
 * 基础实体：继承自 A（A 位于同一包）
 */
public class BaseEntity extends A {
    private long id;      // 私有字段，演示封装
    private String name;

    public BaseEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", name='" + name + "'}";
    }
}