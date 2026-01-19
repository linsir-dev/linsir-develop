package com.linsir.designpattern.strategy;

public class Subtraction implements MathOperation{
    @Override
    public int operate(int a, int b) {
        return a - b;
    }
}
