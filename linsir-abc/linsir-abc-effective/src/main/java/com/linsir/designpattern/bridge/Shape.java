package com.linsir.designpattern.bridge;

// 抽象部分 - 形状类
public abstract class Shape {

    protected  Color color;

    public Shape(Color color) {
        this.color = color;
    }

    public abstract void draw();

}
