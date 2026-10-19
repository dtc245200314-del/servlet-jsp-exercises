package com.codegym.practice;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/customers")
public final class CustomersServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        req.setAttribute("customers",List.of(new Customer("Nguyễn Minh An","1998-03-12","Hà Nội","avatar.svg"),
            new Customer("Trần Bảo Bình","2000-07-25","Đà Nẵng","avatar.svg"),new Customer("Lê Ngọc Chi","1999-11-06","TP. Hồ Chí Minh","avatar.svg")));
        req.getRequestDispatcher("/WEB-INF/customers.jsp").forward(req,res);
    }
}
