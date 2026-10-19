package com.codegym.practice;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/display-discount")
public final class DiscountServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        try {
            String description=req.getParameter("description");
            if(description==null || description.isBlank() || description.length()>500) throw new IllegalArgumentException("Nhập mô tả tối đa 500 ký tự.");
            var price=Calculations.nonNegative(req.getParameter("price"));
            var percent=Calculations.nonNegative(req.getParameter("percent"));
            var amount=Calculations.discount(price,percent);
            req.setAttribute("description",description); req.setAttribute("price",price.toPlainString());
            req.setAttribute("percent",percent.toPlainString()); req.setAttribute("amount",amount.toPlainString());
            req.setAttribute("discountPrice",price.subtract(amount).toPlainString());
            req.getRequestDispatcher("/WEB-INF/discount-result.jsp").forward(req,res);
        } catch(IllegalArgumentException ex) { Html.page(res,400,"Dữ liệu không hợp lệ","<p>"+Html.escape(ex.getMessage())+"</p>"); }
    }
}
