# AGENTS.md - Hướng dẫn & Quy tắc cho AI Agent

## 1. Quy tắc bắt buộc (Mandatory Rules)

- **Ngôn ngữ giao tiếp**: **LUÔN LUÔN phản hồi người dùng bằng Tiếng Việt** trong mọi tình huống (giải thích code, phân tích lỗi, đề xuất giải pháp, lập kế hoạch, v.v.).
- **Ngôn ngữ lập trình / Tên biến**: Giữ nguyên tên biến, tên hàm, tên class, tên bảng/cột cơ sở dữ liệu và commit/code chuẩn bằng Tiếng Anh theo thông lệ quốc tế (trừ các chuỗi hiển thị UI hoặc comment khi được yêu cầu bằng Tiếng Việt).
- **Quản lý Database Schema**: Không tự ý chỉnh sửa các file migration Flyway đã chạy (như `V1__Init_Tables.sql`) trừ khi có yêu cầu rõ ràng. Khi thay đổi cấu trúc DB, hãy tạo file migration mới (`V2__...sql`) vì `spring.jpa.hibernate.ddl-auto` đang được cấu hình là `validate`.

---

## 2. Tổng quan Kiến trúc Dự án

Dự án **Auction Project** là hệ thống sàn đấu giá trực tuyến thời gian thực (Real-time Online Auction Platform), được tổ chức theo mô hình Monorepo gồm 2 phân hệ độc lập (**Backend** và **Frontend**) cùng hạ tầng **Docker Compose**:

```text
auction-project/
├── docker-compose.yml          # Cấu hình hạ tầng MySQL 8.0 và Redis 7
├── auction-backend/            # RESTful API & WebSocket Server (Spring Boot)
│   ├── build.gradle.kts        # Cấu hình Gradle Kotlin DSL & Dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/auction/auctionbackend/
│       │   │   └── AuctionBackendApplication.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/migration/
│       │           └── V1__Init_Tables.sql  # Khởi tạo schema CSDL
│       └── test/               # Unit & Integration Tests
└── auction-frontend/           # Giao diện người dùng (React + Vite)
    ├── package.json            # Dependencies & Scripts cho Frontend
    ├── vite.config.js          # Cấu hình bundler Vite
    └── src/                    # Mã nguồn React components, styles, assets
```

---

## 3. Công nghệ sử dụng (Tech Stack)

### Backend (`auction-backend`)
- **Ngôn ngữ & Runtime**: Java 21 (Toolchain)
- **Framework chính**: Spring Boot `4.1.1`
- **Build Tool**: Gradle (Kotlin DSL - `build.gradle.kts`)
- **Các module & thư viện chính**:
  - **Spring WebMVC**: Xây dựng RESTful APIs.
  - **Spring Data JPA (Hibernate)**: ORM giao tiếp với cơ sở dữ liệu MySQL (`ddl-auto=validate`).
  - **Spring Data Redis**: Caching và xử lý phiên đấu giá / đặt giá thời gian thực.
  - **Spring WebSocket**: Đẩy thông báo và cập nhật giá đấu trực tiếp (real-time bidding).
  - **Spring Security**: Xác thực và phân quyền người dùng (`ROLE_ADMIN`, `ROLE_USER`, `ROLE_SELLER`).
  - **Flyway (`flyway-mysql`)**: Quản lý phiên bản và migration cho cơ sở dữ liệu.
  - **Lombok**: Giảm thiểu boilerplate code (Getters, Setters, Builders, Constructors).
  - **JUnit 5**: Kiểm thử tự động.

### Frontend (`auction-frontend`)
- **Thư viện UI**: React `^19.2.8` & React DOM `^19.2.8` (JavaScript / JSX, ES Modules)
- **Build Tool & Dev Server**: Vite `^8.3.0` (`@vitejs/plugin-react`)
- **Code Quality**: ESLint `^10.10.0` (kèm `eslint-plugin-react-hooks`, `eslint-plugin-react-refresh`)

### Cơ sở dữ liệu & Hạ tầng (`docker-compose.yml`)
- **MySQL 8.0** (`auction_db_container`):
  - Cổng: `3306:3306`
  - Database: `auction_db` (Timezone: `Asia/Ho_Chi_Minh`)
  - Lưu trữ dữ liệu bền vững: Người dùng, phân quyền, danh mục, sản phẩm, phiên đấu giá và lịch sử đặt giá.
- **Redis 7 Alpine** (`auction_redis_container`):
  - Cổng: `6379:6379`
  - Phục vụ caching, quản lý trạng thái đấu giá tốc độ cao và xử lý đồng thời (concurrency) khi đặt giá.

---

## 4. Mô hình Dữ liệu Cốt lõi (Database Schema)

Được định nghĩa tại `auction-backend/src/main/resources/db/migration/V1__Init_Tables.sql`:
1. **`roles`**: Lưu vai trò hệ thống (`ROLE_ADMIN`, `ROLE_USER`, `ROLE_SELLER`).
2. **`users`**: Thông tin tài khoản, thông tin liên hệ và trạng thái (`ACTIVE`, `INACTIVE`, `BANNED`).
3. **`user_roles`**: Bảng trung gian phân quyền nhiều-nhiều giữa `users` và `roles`.
4. **`categories`**: Danh mục sản phẩm (hỗ trợ phân cấp cha-con qua `parent_id`).
5. **`products`**: Sản phẩm đem đấu giá (thuộc về `seller_id`, chứa mảng ảnh dạng `JSON`).
6. **`auctions`**: Phiên đấu giá gắn với sản phẩm (`starting_price`, `step_price`, `current_price`, trạng thái `DRAFT`, `UPCOMING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, thời gian bắt đầu/kết thúc và `winner_id`).
7. **`bids`**: Lịch sử trả giá của người dùng cho từng phiên đấu giá (đánh index `idx_auction_bids (auction_id, bid_amount DESC)` để truy vấn giá cao nhất nhanh chóng).

---

## 5. Lệnh chạy & Phát triển thường dùng

### Khởi động hạ tầng (MySQL & Redis)
```bash
docker compose up -d
```

### Chạy Backend (`auction-backend`)
```bash
cd auction-backend
./gradlew bootRun
# Chạy kiểm thử:
./gradlew test
```

### Chạy Frontend (`auction-frontend`)
```bash
cd auction-frontend
npm install
npm run dev
# Build production:
npm run build
```
