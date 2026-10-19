# Kiểm tra thực tế ngày 19/10/2026

Môi trường: JDK 17, Maven 3.9.16, MySQL 9.4, Tomcat 10.1.60. Dữ liệu kiểm thử dùng schema riêng; không sửa dữ liệu của ứng dụng khác.

- Maven `clean package`: BUILD SUCCESS.
- 10 kiểm thử qua, không bỏ qua: 5 validation và 5 thao tác DAO trên MySQL thật.
- `verify-http.ps1`: 25 kiểm tra HTTP/JSP qua. Đã kiểm tra danh sách, tiếng Việt, tạo mới, điền sẵn khi sửa, cập nhật, xác nhận xóa, POST xóa, 404, input sai, CSRF và escape HTML.
- Đã thao tác qua trình duyệt: thêm sản phẩm kiểm tra giao diện, sửa số lượng từ 3 thành 5, mở trang xác nhận, xác nhận xóa; sản phẩm thử không còn trong danh sách.
- Desktop 1280px: không tràn ngang toàn trang. Mobile 390px: form không tràn; bảng 610px cuộn trong panel, document.scrollWidth = document.clientWidth = 390.
- Toàn bộ JSP/JSPF khai báo UTF-8; dữ liệu mẫu và thông báo tiếng Việt hiển thị đúng.
- Các bản ghi tạo trong kiểm thử CRUD đã được xóa sau kiểm tra; còn 3 sản phẩm mẫu.

File nộp có mã nguồn, schema SQL, pom.xml, hướng dẫn và kiểm thử. Không chứa credential QA hoặc thư mục target.
