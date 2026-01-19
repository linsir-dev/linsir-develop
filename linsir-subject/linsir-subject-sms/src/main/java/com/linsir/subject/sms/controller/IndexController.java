package com.linsir.subject.sms.controller;

import com.linsir.subject.sms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ClassName : IndexController
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-07 15:59
 */

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("")
    public String index()
    {
        return "index";
    }


}
