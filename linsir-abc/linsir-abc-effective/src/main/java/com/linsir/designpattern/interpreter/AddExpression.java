package com.linsir.designpattern.interpreter;

public class AddExpression implements Expression{
    private Expression left;
    private Expression right;

    public AddExpression(final Expression left, final Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();
    }
}
