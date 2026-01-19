package com.linsir.designpattern.mediator;

import java.util.ArrayList;
import java.util.List;

public class ConcreteChatMediator implements ChatMediator{
    private List<User> users = new ArrayList<>();

    @Override
    public void sendMessage(String message, User user) {
        for (User u : users) {
            if (u != user) {
                u.receiveMessage(message);
            }
        }
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }
}
