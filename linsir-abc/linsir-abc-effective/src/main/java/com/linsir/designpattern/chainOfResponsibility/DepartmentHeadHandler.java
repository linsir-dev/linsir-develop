package com.linsir.designpattern.chainOfResponsibility;

public class DepartmentHeadHandler extends ReimbursementHandler{
    @Override
    public void handleRequest(ReimbursementRequest request) {
        if (request.getAmount() <= 5000) {
            System.out.println("部门主管处理报销请求：" + request.getDescription());
        } else if (successor != null) {
            successor.handleRequest(request);
        }
    }
}
