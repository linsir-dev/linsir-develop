package com.linsir.designpattern.chainOfResponsibility;

// 首先，我们需要创建一个表示请求的类 ReimbursementRequest
public class ReimbursementRequest {

    private double amount;
    private String description;

    public ReimbursementRequest(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }
    public double getAmount() {
        return amount;
    }
    public String getDescription() {
        return description;
    }
}
