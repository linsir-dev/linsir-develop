package com.linsir.designpattern.strategy;

public class Multiplication implements MathOperation{
    @Override
    public int operate(int a, int b) {
        return a * b;
    }
}
