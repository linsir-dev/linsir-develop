package com.linsir.abc.servlets;

import com.linsir.abc.dao.StudentDAO;
import com.linsir.abc.dto.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/CheckServlet")
public class CheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String num = request.getParameter("stuNum");
        String pwd = request.getParameter("stuPwd");

        StudentDAO studentDAO = new StudentDAO();
        Student student = null;
        try {
            student = studentDAO.queryStudentByNumAndPwd(num, pwd);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if (student == null){

            request.setAttribute("tips","登录失败，学号或密码错误！");
            request.getRequestDispatcher("login").forward(request,response);

        }else {
            response.sendRedirect("IndexPageServlet");
        }
    }
}
