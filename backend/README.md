# ClothingStore - Backend

## Giới thiệu

**ClothingStore** là dự án backend cho hệ thống quản lý cửa hàng quần áo, được xây dựng với Spring Boot. Dự án cung cấp các API RESTful để quản lý sản phẩm, đơn hàng, người dùng, đánh giá, thanh toán, và upload file. Hệ thống hỗ trợ phân quyền, xác thực bằng JWT, và tích hợp lưu trữ ảnh với Cloudinary.

## Tính năng chính

- Quản lý sản phẩm, danh mục, biến thể sản phẩm (màu sắc, kích cỡ)
- Quản lý đơn hàng, chi tiết đơn hàng, trạng thái đơn hàng
- Quản lý người dùng, phân quyền (role)
- Đăng ký, đăng nhập, xác thực JWT, refresh token
- Đánh giá sản phẩm, quản lý review
- Upload và quản lý file/ảnh sản phẩm (Cloudinary & local storage)
- Thanh toán qua VNPAY (tích hợp sẵn)
- API phân trang, lọc, tìm kiếm
- Xử lý lỗi tập trung, trả về response chuẩn

## Công nghệ sử dụng

- Java 17, Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- MySQL
- Cloudinary (lưu trữ ảnh)
- Lombok
- Maven
- Frontend: Angular 16+ (TypeScript, RxJS, Interceptor JWT, Route Guards,...)
- Repo (FE): https://github.com/NguyenVanThang2004/Frontend_ClothingStore

## Cấu trúc thư mục

- `src/main/java/vn/ClothingStore/` - Mã nguồn chính
  - `controller/` - Các REST API controller
  - `domain/` - Entity, DTO, request/response models
  - `repository/` - JPA repository
  - `service/` - Business logic
  - `specifications/` - Dynamic query cho filter/search
  - `util/` - Tiện ích, xử lý lỗi, bảo mật
  - `config/` - Cấu hình bảo mật, Cloudinary, CORS, v.v.
- `src/main/resources/` - Cấu hình ứng dụng, template, static files
- `src/test/java/` - Unit test

## Hướng dẫn chạy dự án

### 1) Clone dự án
```bash
git clone https://github.com/NguyenVanThang2004/backend_ClothingStore.git
cd backend_ClothingStore
2) Cấu hình file .env
Tạo file .env ở thư mục gốc dự án (cùng cấp pom.xml) và điền các biến sau:

env
DB_URL=jdbc:mysql://localhost:3306/clothingstore
DB_USERNAME=root
DB_PASSWORD=123456

CLOUDINARY_CLOUD_NAME=xxx
CLOUDINARY_API_KEY=xxx
CLOUDINARY_API_SECRET=xxx

JWT_SECRET=your_jwt_secret
Gợi ý: Có thể thêm ?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC vào DB_URL nếu cần.

3) Chạy ứng dụng
bash
./mvnw spring-boot:run
# hoặc
mvn spring-boot:run
