package com.linsir.subject.mvc.controller;



import com.linsir.subject.mvc.annotation.LinsirAutowired;
import com.linsir.subject.mvc.annotation.LinsirController;
import com.linsir.subject.mvc.annotation.LinsirRquestMapping;
import com.linsir.subject.mvc.annotation.LinsirRquestParam;
import com.linsir.subject.mvc.service.LService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/1 23:20
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
@LinsirController
@LinsirRquestMapping("/L")
public class LController {


    @LinsirAutowired("lServiceImpl")
    private LService lService;

    @LinsirRquestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @LinsirRquestParam("name") String name, @LinsirRquestParam("age") String age) {
            try {
                PrintWriter writer =response.getWriter();
                String  result = lService.query(name,age);
                writer.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
