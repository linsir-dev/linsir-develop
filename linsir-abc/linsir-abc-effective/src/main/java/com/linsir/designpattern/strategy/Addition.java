package com.linsir.designpattern.strategy;

// 实现加法策略

public class Addition implements MathOperation{
    @Override
    public int operate(int a, int b) {
        return a + b;
    }
}
