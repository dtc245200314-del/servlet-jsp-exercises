# Final Exam — Quản lý sản phẩm

**File nộp:** [DTC245200314_FinalExam.zip](dist/DTC245200314_FinalExam.zip) — giải nén rồi thực hiện hướng dẫn bên dưới.

Ứng dụng đáp ứng bài CodeGym 544: danh sách sản phẩm, thêm, sửa có dữ liệu điền sẵn, xác nhận xóa, lưu trữ JDBC/MySQL. Mỗi sản phẩm có `id`, `name`, `price`, `quantity`. Dự án dùng Java 17, Jakarta Servlet 6, JSP/JSTL và Maven, chạy trên Tomcat 10.1+.

## Chạy ứng dụng

1. Tạo cơ sở dữ liệu thực hành mới bằng `database.sql` (MySQL 8.0.16+ hoặc MySQL 9.x). Script tạo bảng và 3 sản phẩm mẫu, không xóa bảng đang có.
2. Cấu hình các biến môi trường cho tiến trình Tomcat:

```text
PRODUCT_DB_URL=jdbc:mysql://127.0.0.1:3306/codegym_final?characterEncoding=UTF-8
DB_USER=your_mysql_user
DB_PASSWORD=your_mysql_password
```

3. Chạy `mvn clean package`. Copy `target/final-exam.war` vào `webapps/` của Tomcat. Khởi động Tomcat sau khi đã cấu hình môi trường.
4. Mở `http://localhost:8080/final-exam/`. Trang chủ tự chuyển tới `/products`.

Trên PowerShell có thể đặt biến cho phiên hiện tại bằng `$env:PRODUCT_DB_URL='...'`, `$env:DB_USER='...'`, `$env:DB_PASSWORD='...'`, rồi khởi động Tomcat từ chính phiên đó. Không đặt mật khẩu trong mã nguồn hoặc commit file cấu hình riêng.

## Cấu trúc và luồng xử lý

- `model/Product.java`: dữ liệu sản phẩm, giá dùng BigDecimal.
- `dao/ProductDAO.java`: SELECT/INSERT/UPDATE/DELETE bằng PreparedStatement, try-with-resources.
- `service/ProductValidator.java`: tên bắt buộc, giá/số lượng không âm, giới hạn kiểu dữ liệu.
- `controller/ProductServlet.java`: nhận request, gọi DAO, forward JSP; POST thành công redirect về danh sách (PRG).
- `WEB-INF/views/`: JSP sử dụng JSTL/EL, escape dữ liệu bằng c:out, mã hóa UTF-8.
- `database.sql`: schema và dữ liệu mẫu.

GET `products?action=delete&id=...` chỉ hiển thị trang xác nhận. Dữ liệu chỉ bị xóa khi gửi POST xác nhận với CSRF token hợp lệ. Không có tài khoản hay dữ liệu người dùng thật trong dự án.

## Kiểm tra

`mvn test` chạy 5 kiểm thử validation. Để chạy thêm 5 kiểm thử trên MySQL thật, đặt `RUN_DB_TESTS=true`, `PRODUCT_DB_URL`, `DB_USER`, `DB_PASSWORD` trỏ tới cơ sở dữ liệu thực hành đã import schema. Không chạy các kiểm thử ghi dữ liệu trên production.

Kiểm tra HTTP/JSP và giao diện được ghi trong QA.md sau khi chạy thực tế. File nộp chứa toàn bộ mã nguồn, pom.xml, SQL, hướng dẫn và kiểm thử; không chứa mật khẩu hay thư mục target.

Mã nguồn do Codex triển khai theo yêu cầu người dùng; không sao chép dự án mẫu từ bên thứ ba. Các dependency được khai báo và khóa phiên bản trong pom.xml.
