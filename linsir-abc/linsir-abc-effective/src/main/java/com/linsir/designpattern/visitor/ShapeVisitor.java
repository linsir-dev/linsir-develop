package com.linsir.designpattern.visitor;


// 接下来，定义一个访问者接口和具体的访问者实现
// 访问者接口
public interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
}
