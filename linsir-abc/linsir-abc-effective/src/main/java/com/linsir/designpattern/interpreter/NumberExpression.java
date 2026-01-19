package com.linsir.designpattern.interpreter;

// 数字表达式类
public class NumberExpression implements Expression{

    private int value;

    public NumberExpression(int value) {
        this.value = value;
    }



    @Override
    public int interpret() {
        return value;
    }
}
