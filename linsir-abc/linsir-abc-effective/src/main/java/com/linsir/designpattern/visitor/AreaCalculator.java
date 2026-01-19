package com.linsir.designpattern.visitor;

public class AreaCalculator  implements ShapeVisitor{

    private double area;

    @Override
    public void visit(Circle circle) {
        area += Math.PI * circle.getRadius() * circle.getRadius();
    }

    @Override
    public void visit(Rectangle rectangle) {
        area += rectangle.getWidth() * rectangle.getHeight();
    }

    public double getArea() {
        return area;
    }
}
