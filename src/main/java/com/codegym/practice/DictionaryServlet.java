package com.codegym.practice;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/translate")
public final class DictionaryServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String word=req.getParameter("word");
        String meaning=Calculations.translate(word);
        Html.page(res, 200, "Từ điển Servlet", "<p>Từ khóa: " + Html.escape(word) + "</p><p>" + (meaning==null ? "Không tìm thấy từ." : Html.escape(meaning)) + "</p>");
    }
}
