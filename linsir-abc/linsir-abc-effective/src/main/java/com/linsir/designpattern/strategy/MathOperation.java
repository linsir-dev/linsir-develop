package com.linsir.designpattern.strategy;

// 首先，我们定义一个接口 MathOperation，表示数学操作的策略
// 定义策略接口

public interface MathOperation {

    int operate(int a, int b);

}
