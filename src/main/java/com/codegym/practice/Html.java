package com.codegym.practice;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

public final class Html {
    private Html() {}
    public static String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
    public static void page(HttpServletResponse response, int status, String title, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<!doctype html><html lang=\"vi\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>"
            + escape(title) + "</title><link rel=\"stylesheet\" href=\"style.css\"></head><body><main><h1>"
            + escape(title) + "</h1>" + body + "<p><a href=\"index.jsp\">Về trang bài tập</a></p></main></body></html>");
    }
}
