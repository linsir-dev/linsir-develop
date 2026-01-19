package com.linsir.controller;


import com.linsir.entity.User;
import com.linsir.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class LoginController {


    @Autowired
    private UserService userService;

    @GetMapping("login/{username}/{password}")
    public String login(@PathVariable("username") String username, @PathVariable("password") String password, HttpServletRequest request) {
       User user = userService.getUserByUsername(username);
       if (user != null && user.getPassword().equals(password)) {
           request.getSession().setAttribute("user", user);
           return "success";
       }
       return "fail";
    }

    @GetMapping("getUser")
    public String getCurrentUser(HttpSession session) {
        return session.getAttribute("user").toString();
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "logout success";
    }

}
