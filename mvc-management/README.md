# MVC Customer & Product Management

Bài498: Customer model, CustomerService/CustomerServiceImpl, CustomerServlet và JSP cho danh sách, thêm, sửa, xem chi tiết, xác nhận xóa.

Bài499: Product(id,name,price,description,manufacturer), ProductService/ProductServiceImpl, ProductServlet; đủ CRUD, chi tiết và tìm sản phẩm theo tên.

Dữ liệu mẫu giả lập lưu trong bộ nhớ, reset khi khởi động lại ứng dụng. Dùng Java17, Maven, Tomcat10.1; chạy mvn clean package, deploy target/mvc-management.war rồi truy cập /mvc-management/customers và /mvc-management/products. Không cần MySQL cho hai bài MVC này.

Các thao tác thay đổi dùng POST, CSRF và PRG; xóa có trang xác nhận; JSP escape nội dung qua c:out; form kiểm tra dữ liệu ở cả trình duyệt và Servlet. Các bài có JDBC nằm riêng trong ../user-jdbc-management.

Mã do Codex hỗ trợ thực hiện theo yêu cầu người dùng.

## Validation — 20 October 2026

Java 17, Tomcat 10.1.60, dedicated MySQL 26.7 schema. MVC: 3 JUnit tests passed. JDBC: 8 integration tests passed, 0 skipped. Both WAR builds succeeded. The browser rendered all JSP pages. Automated HTTP checks: 69/69 passed across MVC customer/product CRUD, prepared/stored user CRUD, country search, CSRF, escaped output, 400/404 responses, and transaction experiments (partial commit, rollback, full commit, invalid permission FK rollback).

Reproduce HTTP checks after deploying both WARs on localhost port 18091: run the parent verify-mvc-jdbc-http.py with Python 3. It uses fictional test data and removes its CRUD records; transaction demonstrations intentionally retain Employee rows. No credentials or machine-specific connection files are published.
