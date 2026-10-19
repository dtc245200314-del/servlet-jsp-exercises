# Servlet + JSP + JSTL exercises

Nine exercises in one deployable WAR, with independent routes and forms. Java 17, Jakarta Servlet 6, Tomcat 10.1+, Maven 3.9+. Build `mvn clean package`, copy `target/practice.war` to Tomcat `webapps/`, visit `http://localhost:8080/practice/`.

| CodeGym activity | Route and implementation |
| --- | --- |
| 470 System time | `/hello` (`/time` alias), TimeServlet reads server time on each GET; Cache-Control no-store |
| 471 Login | Home form POST `/login`, admin/admin as explicitly required by the exercise; greet or HTTP 401 Login Error |
| 472 Currency Servlet | Home form POST `/convert`, input rate and USD; exact BigDecimal multiplication |
| 473 Dictionary Servlet | Home form POST `/translate`, case-insensitive English–Vietnamese lookup |
| 474 Discount calculator | Home form POST `/display-discount`, description, list price, percent, amount and final price |
| 486 Currency JSP | `/jsp-currency.jsp` POST to `/converter.jsp`; scriptlet calculation and JSP expression |
| 487 Dictionary JSP | `/jsp-dictionary.jsp` POST to `/dictionary.jsp`; Map, scriptlet lookup and escaped JSP expression |
| 488 Customer list | `/customers`, Servlet sets list; JSP uses c:forEach and c:out for name, birthday, address, image |
| 489 Calculator | `/calculator`, Servlet validates and calculates; JSP renders result/error including division by zero |

JSP scriptlets are used only in the two exercises that explicitly teach them. Later views use EL/JSTL and keep calculations in Java. Monetary inputs are nonnegative, percent is 0–100, nonfinite/oversized numbers are rejected, submitted text is HTML escaped. Division uses DECIMAL64 rounding. Currency rates are user-provided examples, not current financial data. Login is a fixed-credential teaching demonstration, not production authentication. Customer data and avatar are fictional/original.

`mvn test` checks arithmetic, discount boundaries, invalid numeric input, division by zero, dictionary normalization and HTML escaping. HTTP/JSP deployment verification is recorded separately after execution. Dependencies are pinned in pom.xml. Source is original implementation with Codex assistance based on CodeGym's requirements.

Đã chạy trên Tomcat 10.1.60 / JDK 17: 24 kiểm tra HTTP/JSP đều qua, gồm render JSTL, UTF-8, dữ liệu sai, XSS được escape và chia 0. Kết quả lưu trong http-verification.log; chạy lại bằng PowerShell 7: ./verify-http.ps1 -BaseUrl http://localhost:8080/practice .
