package com.linsir.abc.servlets;

import com.linsir.abc.dao.GradeDAO;
import com.linsir.abc.dto.Grade;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/GradeQueryServlet")
public class GradeQueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String snum = request.getParameter("stuNum");
    String cid = request.getParameter("courseId");

    GradeDAO gradeDAO = new GradeDAO();
    Grade grade = gradeDAO.queryGradeBySnumAndCid(snum, cid);

    request.setAttribute("grade",grade);
    request.getRequestDispatcher("GradePageServlet").forward(request,response);
    }
}
