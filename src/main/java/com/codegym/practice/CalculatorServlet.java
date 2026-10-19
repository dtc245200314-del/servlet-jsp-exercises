package com.codegym.practice;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/calculator")
public final class CalculatorServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        req.getRequestDispatcher("/WEB-INF/calculator.jsp").forward(req,res);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        req.setCharacterEncoding("UTF-8");
        try {
            var result=Calculations.calculate(Calculations.number(req.getParameter("left")),Calculations.number(req.getParameter("right")),req.getParameter("operator"));
            req.setAttribute("result",result.stripTrailingZeros().toPlainString());
        } catch(IllegalArgumentException ex) {res.setStatus(400); req.setAttribute("error",ex.getMessage());}
        doGet(req,res);
    }
}
