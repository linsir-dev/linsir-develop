package com.linsir.designpattern.strategy;

public class Calculator {

    private MathOperation operation;

    public void setOperation(MathOperation operation) {
        this.operation = operation;
    }

    public int performOperation(int a, int b) {
        if (operation != null) {
            return operation.operate(a, b);
        }
        throw new IllegalStateException("No operation set");
    }
}
