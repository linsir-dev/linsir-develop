package com.linsir.designpattern.mediator;


// 中介者接口
public interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}
