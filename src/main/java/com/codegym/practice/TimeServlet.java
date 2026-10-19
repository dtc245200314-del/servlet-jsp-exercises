package com.codegym.practice;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@WebServlet({"/hello", "/time"})
public final class TimeServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setHeader("Cache-Control", "no-store");
        Html.page(res, 200, "Thời gian hệ thống", "<p id=\"server-time\">" + Html.escape(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))) + "</p>");
    }
}
