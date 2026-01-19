package com.linsir.designpattern.flyweight;

public class Circle implements Shape {

    private Yanse yanse;

    public Circle(Yanse yanse) {
        this.yanse = yanse;
    }

    @Override
    public void draw(int x, int y) {
        System.out.println("Drawing a " + yanse + " circle at (" + x + "," + y + ")");
    }
}
