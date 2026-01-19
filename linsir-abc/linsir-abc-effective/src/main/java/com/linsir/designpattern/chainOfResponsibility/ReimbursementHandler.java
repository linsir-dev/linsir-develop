package com.linsir.designpattern.chainOfResponsibility;

// 然后，创建一个抽象处理者类 ReimbursementHandler
public abstract class ReimbursementHandler {

    protected ReimbursementHandler successor;

    public void setSuccessor(ReimbursementHandler successor) {
        this.successor = successor;
    }

    public abstract void handleRequest(ReimbursementRequest request);
}
