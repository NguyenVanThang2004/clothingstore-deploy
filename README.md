# ClothingStore – Triển khai (Deploy)

Triển khai full-stack (Angular + Spring Boot + MySQL + Nginx) cho dự án ClothingStore.

🌐 Demo (HTTP): http://nguyenvanthang07.io.vn  
📦 Công nghệ: Docker, Docker Compose, Nginx, MySQL 8, Spring Boot 3, Angular

## Liên kết repo
🔧 Backend (Spring Boot): https://github.com/NguyenVanThang2004/backend_ClothingStore  
🎨 Frontend (Angular): https://github.com/NguyenVanThang2004/Frontend_ClothingStore

## Kiến trúc
Nginx (serve FE + proxy `/api`) → Spring Boot (8080) → MySQL (3306)

## Khởi chạy nhanh (production)
```bash
# 1) Chuẩn bị biến môi trường cho backend
#   - Tạo file backend/.env (DB_URL, DB_USERNAME, DB_PASSWORD, VNPAY_RETURN_URL, ...)

# 2) Build & run
cd production
docker compose -p clothingstore-production up -d --build
