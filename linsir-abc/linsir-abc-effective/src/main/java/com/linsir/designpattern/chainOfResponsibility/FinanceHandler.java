package com.linsir.designpattern.chainOfResponsibility;

public class FinanceHandler extends ReimbursementHandler{
    @Override
    public void handleRequest(ReimbursementRequest request) {
        System.out.println("财务部门处理报销请求：" + request.getDescription());
    }
}
