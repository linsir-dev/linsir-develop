package com.linsir.designpattern.chainOfResponsibility;

// 接下来，实现具体的处理者类：经理、部门主管和财务部门处理者。
public class ManagerHandler extends ReimbursementHandler{


    @Override
    public void handleRequest(ReimbursementRequest request) {
        if (request.getAmount() <= 1000) {
            System.out.println("经理处理报销请求：" + request.getDescription());
        } else if (successor != null) {
            successor.handleRequest(request);
        }
    }
}
