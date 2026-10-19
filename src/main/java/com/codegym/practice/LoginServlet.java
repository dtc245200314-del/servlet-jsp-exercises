package com.codegym.practice;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public final class LoginServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-store");
        boolean valid = "admin".equals(req.getParameter("username")) && "admin".equals(req.getParameter("password"));
        Html.page(res, valid ? 200 : 401, valid ? "Welcome admin to website" : "Login Error", "<p>Demo Servlet POST theo đề bài; không tạo phiên đăng nhập.</p>");
    }
}
