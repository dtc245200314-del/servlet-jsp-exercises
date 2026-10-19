package com.codegym.practice;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/convert")
public final class CurrencyServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            var amount = Calculations.currency(req.getParameter("rate"), req.getParameter("usd"));
            Html.page(res, 200, "Kết quả chuyển đổi Servlet", "<p>Thành tiền: <strong>" + amount.toPlainString() + " VNĐ</strong></p>");
        } catch (IllegalArgumentException ex) { Html.page(res,400,"Dữ liệu không hợp lệ","<p>"+Html.escape(ex.getMessage())+"</p>"); }
    }
}
