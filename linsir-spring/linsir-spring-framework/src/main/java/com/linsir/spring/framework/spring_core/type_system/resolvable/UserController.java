package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.util.List;

/**
 * UserController for demonstrating method parameter type resolution
 */
public class UserController {

    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    /**
     * Method with generic return type: List<User>
     */
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    /**
     * Method with generic parameter
     */
    public void processUsers(List<User> users) {
        for (User user : users) {
            System.out.println("Processing: " + user);
        }
    }

    /**
     * Generic method with type variable
     */
    public <T extends User> T findUser(Long id, Class<T> type) {
        return type.cast(userService.findById(id));
    }

    /**
     * Method with array type
     */
    public User[] getUsersAsArray() {
        return userService.findAll().toArray(new User[0]);
    }

    /**
     * Method with nested generic type
     */
    public java.util.Map<String, List<User>> groupUsersByName() {
        java.util.Map<String, List<User>> result = new java.util.HashMap<>();
        for (User user : userService.findAll()) {
            result.computeIfAbsent(user.getName(), k -> new java.util.ArrayList<>()).add(user);
        }
        return result;
    }
}
