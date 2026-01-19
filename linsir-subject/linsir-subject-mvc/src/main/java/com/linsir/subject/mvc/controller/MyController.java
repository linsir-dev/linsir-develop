package com.linsir.subject.mvc.controller;

import com.linsir.subject.mvc.annotation.LinsirAutowired;
import com.linsir.subject.mvc.annotation.LinsirController;
import com.linsir.subject.mvc.annotation.LinsirRquestMapping;
import com.linsir.subject.mvc.annotation.LinsirRquestParam;
import com.linsir.subject.mvc.service.LService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/2 13:18
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
@LinsirController
@LinsirRquestMapping("/my")
public class MyController {

    @LinsirAutowired("lServiceImpl")
    private LService lService;

    @LinsirRquestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @LinsirRquestParam("name") String name, @LinsirRquestParam("age") String age) {

        String result=lService.query(name,age);

        request.setAttribute("result",result+"MYMYMYMY");

        try {
            request.getRequestDispatcher("/WEB-INF/my.jsp").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
