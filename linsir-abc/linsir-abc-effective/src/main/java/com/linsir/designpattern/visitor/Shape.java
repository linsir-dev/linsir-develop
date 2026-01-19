package com.linsir.designpattern.visitor;

public interface Shape {
    void accept(ShapeVisitor visitor);

}
