# AGENTS.md - Hướng dẫn, Quy tắc Kỹ thuật & Lộ trình Phát triển cho AI Agent

Tài liệu này là nguồn sự thật duy nhất (Single Source of Truth) về kiến trúc, quy ước kỹ thuật và lộ trình phát triển của dự án **Auction Project**. Mọi AI Agent (như `agy`) khi hỗ trợ dự án này BẮT BUỘC phải đọc và tuân thủ nghiêm ngặt các quy tắc dưới đây.

---

## 1. Quy tắc bắt buộc & Vai trò của AI Agent (Mandatory Rules)

- **Ngôn ngữ giao tiếp**: **LUÔN LUÔN phản hồi người dùng bằng Tiếng Việt** trong mọi tình huống (giải thích code, phân tích lỗi, đề xuất giải pháp, lập kế hoạch, v.v.).
- **Vai trò Kỹ sư / Người thầy nghiêm khắc**: Không mặc định đồng ý với mọi ý kiến hoặc cách làm của người dùng nếu cách đó vi phạm Clean Architecture, gây lỗi bảo mật, hoặc làm giảm hiệu năng (N+1 query, Race Condition, lặp code). Phải chỉ rõ chỗ sai và đưa ra giải pháp tối ưu nhất.
- **Ngôn ngữ lập trình / Tên biến**: Giữ nguyên tên biến, tên hàm, tên class, tên bảng/cột cơ sở dữ liệu bằng Tiếng Anh theo thông lệ quốc tế. Commit message theo chuẩn Conventional Commits (`feat`, `fix`, `chore`, `refactor`...).
- **Quản lý Database Schema**: Không tự ý chỉnh sửa các file migration Flyway đã chạy (`V1` -> `V4`) trừ khi có yêu cầu rõ ràng. Khi thay đổi cấu trúc DB, hãy tạo file migration mới vì `spring.jpa.hibernate.ddl-auto` đang được cấu hình là `validate`.
- **Quy ước Code Backend (Spring Boot & Java 21)**:
  - **DTO Pattern**: Tuyệt đối không trả trực tiếp `@Entity` ra `Controller` hoặc nhận `@Entity` làm Request Body. Mọi class trong `dto/request` và `dto/response` bắt buộc dùng `record` của Java 21 để đảm bảo tính bất biến (Immutability).
  - **Data Mapping**: Không viết hàm chuyển đổi `private` lặp lại trong `Service`. Sử dụng **Static Factory Method** `fromEntity(...)` đặt trực tiếp bên trong `record` Response (ví dụ: `UserResponse.fromEntity(user)`), và `PageResponse.fromPage(page)` cho phân trang.
  - **Chuẩn hóa Response & Exception**: Mọi API thành công đều bọc trong `ApiResponse<T>`. Mọi lỗi nghiệp vụ đều ném `throw new AppException(ErrorCode.XXX)` và được xử lý tập trung tại `GlobalExceptionHandler` (trả về `ErrorResponse`).
  - **Transaction & Concurrency**: Hàm ghi dữ liệu (`INSERT`, `UPDATE`, `DELETE`) phải gắn `@Transactional`. Hàm chỉ đọc dữ liệu (`SELECT`) phải gắn `@Transactional(readOnly = true)`. Mọi thao tác biến động số dư (`balance`, `locked_balance`) hoặc đặt giá phải sử dụng Pessimistic Write Lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) hoặc Redis Distributed Lock.

---

## 2. Tổng quan Kiến trúc Dự án

Dự án **Auction Project** là hệ thống sàn đấu giá trực tuyến thời gian thực (Real-time Online Auction Platform), được tổ chức theo mô hình Monorepo gồm 2 phân hệ độc lập (**Backend** và **Frontend**) cùng hạ tầng **Docker Compose**:

```text
auction-project/
├── docker-compose.yml          # Cấu hình hạ tầng MySQL 8.0 và Redis 7
├── AGENTS.md                   # Ngữ cảnh hệ thống và lộ trình phát triển cho AI Agent
├── auction-backend/            # RESTful API & WebSocket Server (Spring Boot)
│   ├── build.gradle.kts        # Cấu hình Gradle Kotlin DSL & Dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/auction/auctionbackend/
│       │   │   ├── config/     # SecurityConfig, WebSocketConfig, CloudinaryConfig...
│       │   │   ├── controller/ # REST Controllers
│       │   │   ├── dto/        # Java 21 Records (request / response)
│       │   │   ├── entity/     # JPA Entities
│       │   │   ├── enums/      # Enums hệ thống
│       │   │   ├── exception/  # AppException, ErrorCode, GlobalExceptionHandler
│       │   │   ├── repository/ # Spring Data JPA Repositories
│       │   │   ├── security/   # JwtService, JwtAuthenticationFilter, CustomUserDetailsService
│       │   │   └── service/    # Business Logic Services
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/migration/
│       │           ├── V1__Init_Tables.sql
│       │           ├── V2__Add_Wallet_And_Transactions.sql
│       │           ├── V3__Convert_Enums_To_Varchar.sql
│       │           └── V4__Seed_Initial_Roles.sql
│       └── test/               # Unit, Integration & Concurrency Tests
└── auction-frontend/           # Giao diện người dùng (React + Vite)
    ├── package.json            # Dependencies & Scripts cho Frontend
    ├── vite.config.js          # Cấu hình bundler Vite
    └── src/                    # Mã nguồn React components, contexts, pages, services
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
  - **Spring WebSocket (STOMP)**: Đẩy thông báo và cập nhật giá đấu trực tiếp (real-time bidding).
  - **Spring Security 6 & JJWT (`0.12.6`)**: Xác thực Stateless JWT và phân quyền người dùng (`ROLE_ADMIN`, `ROLE_USER`, `ROLE_SELLER`).
  - **Flyway (`flyway-mysql`)**: Quản lý phiên bản và migration cho cơ sở dữ liệu.
  - **Jakarta Validation**: Kiểm tra dữ liệu đầu vào (`@Valid`, `@NotBlank`, `@Pattern`, `@DecimalMin`...).
  - **Lombok**: Giảm thiểu boilerplate code cho Entity và Service.
  - **JUnit 5**: Kiểm thử tự động và kiểm thử đa luồng (`ExecutorService`, `CountDownLatch`).

### Frontend (`auction-frontend`)
- **Thư viện UI**: React `^19.2.8` & React DOM `^19.2.8` (JavaScript / JSX, ES Modules)
- **Build Tool & Dev Server**: Vite `^8.3.0` (`@vitejs/plugin-react`)
- **Thư viện tích hợp**: `axios`, `react-router-dom`, `@stomp/stompjs`
- **Code Quality**: ESLint `^10.10.0` (kèm `eslint-plugin-react-hooks`, `eslint-plugin-react-refresh`)

### Cơ sở dữ liệu & Hạ tầng (`docker-compose.yml`)
- **MySQL 8.0** (`auction_db_container`): Cổng `3306:3306`, Database `auction_db` (Timezone: `Asia/Ho_Chi_Minh`).
- **Redis 7 Alpine** (`auction_redis_container`): Cổng `6379:6379`, phục vụ caching, quản lý trạng thái đấu giá tốc độ cao và xử lý đồng thời (concurrency) khi đặt giá.

---

## 4. Mô hình Dữ liệu Cốt lõi (Database Schema từ V1 -> V4)

Được định nghĩa tại `auction-backend/src/main/resources/db/migration/`:
1. **`roles`** (`V1`, `V4`): Lưu vai trò hệ thống (`id`, `name`: `ROLE_ADMIN`, `ROLE_USER`, `ROLE_SELLER`).
2. **`users`** (`V1`, `V2`, `V3`): Thông tin tài khoản (`id`, `username`, `password`, `email`, `full_name`, `phone`, `address`), trạng thái `status` (`VARCHAR(30)`: `ACTIVE`, `INACTIVE`, `BANNED`), số dư khả dụng `balance` (`DECIMAL(15,2)`), số dư đóng băng `locked_balance` (`DECIMAL(15,2)`), `created_at`, `updated_at`. *(Lưu ý: Bảng `users` KHÔNG có cột `avatar_url`)*.
3. **`user_roles`** (`V1`): Bảng trung gian phân quyền nhiều-nhiều (`user_id`, `role_id`).
4. **`wallet_transactions`** (`V2`, `V3`): Lịch sử biến động số dư (`id`, `user_id`, `auction_id`, `amount`, `type` [`DEPOSIT`, `WITHDRAW`, `BID_LOCK`, `BID_UNLOCK`, `AUCTION_PAYMENT`, `SELLER_PAYOUT`], `status` [`PENDING`, `COMPLETED`, `FAILED`, `CANCELLED`], `description`, `created_at`). Đánh index `idx_wallet_tx_user_date (user_id, created_at DESC)`.
5. **`categories`** (`V1`): Danh mục sản phẩm (`id`, `name`, `description`, hỗ trợ phân cấp cha-con qua `parent_id`).
6. **`products`** (`V1`): Sản phẩm đem đấu giá (`id`, `seller_id`, `category_id`, `name`, `description`, mảng ảnh `image_urls` kiểu `JSON`, `created_at`).
7. **`auctions`** (`V1`, `V3`): Phiên đấu giá gắn 1-1 với sản phẩm (`id`, `product_id` UNIQUE, `starting_price`, `step_price`, `current_price`, trạng thái `status` `VARCHAR(30)` [`DRAFT`, `UPCOMING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`], `start_time`, `end_time`, `winner_id`, `created_at`, `updated_at`).
8. **`bids`** (`V1`): Lịch sử trả giá (`id`, `auction_id`, `bidder_id`, `bid_amount`, `bid_time`). Đánh index `idx_auction_bids (auction_id, bid_amount DESC)`.

---

## 5. Lộ trình Phát triển Chi tiết & Tiến độ Hiện tại (Project Roadmap)

> **Quy tắc cho AI Agent:** Khi người dùng yêu cầu "tiếp tục bước tiếp theo", hãy kiểm tra mục có đánh dấu `[🔄 IN PROGRESS]` hoặc mục `[ ]` đầu tiên ngay sau mục `[x]` cuối cùng. Sau khi hoàn thành và kiểm thử xong một bước, hãy nhắc người dùng cập nhật `[x]` vào file `AGENTS.md` trước khi merge nhánh.

### Giai đoạn 1: Hạt nhân Hệ thống, Bảo mật & Ví tiền (Foundation, Auth & Wallet)
Mục tiêu là xây dựng khung xử lý lỗi tập trung, xác thực người dùng và cấp tiền vào ví để chuẩn bị dữ liệu cho việc đấu giá.

- [x] **1. Khởi tạo dữ liệu quyền (`chore/seed-roles`)**:
  - Tạo file migration (`V4__Seed_Initial_Roles.sql`) để nạp sẵn 3 quyền gốc: `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN` vào bảng `roles`.
- [x] **2. Xử lý Ngoại lệ & Chuẩn hóa Phản hồi (`feat/core-exception-handler`)**:
  - Thiết kế cấu trúc trả về đồng nhất cho toàn hệ thống: `ApiResponse<T>` và `ErrorResponse`.
  - Xây dựng `@RestControllerAdvice` (`GlobalExceptionHandler`) để bắt toàn bộ lỗi (Validation, sai mật khẩu, không đủ quyền, hết tiền) và trả về HTTP Status chuẩn xác.
- [x] **3. Xác thực & Phân quyền JWT (`feat/user-authentication`)**:
  - Tạo đúng 2 Entity: `Role` và `User` (ánh xạ đầy đủ cả `balance` và `locked_balance`).
  - Cấu hình Spring Security 6, bộ lọc `JwtAuthenticationFilter` và mã hóa mật khẩu `BCryptPasswordEncoder`.
  - Xây dựng API: `POST /api/v1/auth/register` (mặc định gán `ROLE_USER`), `POST /api/v1/auth/login` (trả về Access Token), và `GET /api/v1/auth/me` (hoặc `/api/v1/users/me` - lấy thông tin cá nhân kèm số dư ví).
- [x] **4. Nghiệp vụ Ví tiền (`feat/user-wallet`)**:
  - Tạo Entity: `WalletTransaction`.
  - Xây dựng API nạp tiền (`POST /api/v1/wallet/deposit` — cộng `balance` và ghi log `DEPOSIT`), rút tiền (`POST /api/v1/wallet/withdraw`), và API xem lịch sử biến động số dư có phân trang (`GET /api/v1/wallet/transactions`).

---

### Giai đoạn 2: Quản lý Danh mục, Sản phẩm & Lưu trữ Đám mây (Catalog & Media)
Giải quyết bài toán phân loại hàng hóa đa cấp và lưu trữ hình ảnh tách biệt khỏi máy chủ ứng dụng.

- [x] **1. Danh mục đa cấp (`feat/category-management`)**:
  - Tạo Entity: `Category` (cấu hình quan hệ tự tham chiếu `@ManyToOne` cho `parent` và `@OneToMany` cho `children`).
  - Xây dựng API CRUD danh mục (chỉ `ROLE_ADMIN` được thêm/sửa/xóa; mọi người đều được xem danh sách dạng cây phân cấp).
- [x] **2. Sản phẩm & Tải ảnh lên Cloud (`feat/product-management`)**:
  - Tích hợp **Cloudinary SDK** (hoặc MinIO) để nhận file ảnh (`MultipartFile`), đẩy lên đám mây và thu về đường dẫn URL.
  - Tạo Entity: `Product` (ánh xạ cột `image_urls` kiểu JSON sang `List<String>` trong Java).
  - Xây dựng API cho phép người bán (`ROLE_SELLER`) thêm mới, cập nhật sản phẩm của chính họ và API nâng cấp tài khoản từ người mua lên người bán.

---

### Giai đoạn 3: Thiết lập & Truy vấn Phiên đấu giá (Auction Setup & Discovery)
Tách biệt hoàn toàn việc "Quản lý thông tin phiên" và "Hành động đặt giá".

- [x] **1. Mở phiên đấu giá (`feat/auction-creation`)**:
  - Tạo Entity: `Auction`.
  - Xây dựng API `POST /api/v1/auctions`: Kiểm tra nghiêm ngặt sản phẩm có thuộc về Seller đó không, sản phẩm đã nằm trong phiên nào khác chưa, `start_time` phải ở tương lai, `end_time > start_time`, `starting_price > 0` và `step_price > 0`.
- [x] **2. Tìm kiếm & Lọc nâng cao (`feat/auction-search`)**:
  - Sử dụng **Spring Data JPA Specification** kết hợp `Pageable` để viết API `GET /api/v1/auctions`: Hỗ trợ tìm theo từ khóa tên sản phẩm, lọc theo `category_id`, lọc theo trạng thái (`UPCOMING`, `IN_PROGRESS`, `COMPLETED`), lọc theo khoảng giá và sắp xếp theo thời gian kết thúc.

---

### Giai đoạn 4: Trái tim Hệ thống — Đặt giá Đa luồng & Thời gian thực (Bidding Engine)
Áp dụng kiến trúc **Ghi qua REST API, Đọc qua WebSocket** và kiểm soát chặt chẽ tính toàn vẹn tài chính dưới tải cao.

- [🔄 IN PROGRESS] **1. Cấu hình WebSocket (`feat/websocket-config`)**:
  - Thiết lập STOMP over WebSocket với broker `/topic`. Khi có biến động ở phiên đấu giá nào, server sẽ bắn bản tin vào kênh `/topic/auctions/{auctionId}`.
- [ ] **2. Xử lý Đặt giá & Khóa dòng tiền (`feat/bidding-engine`)**:
  - Tạo Entity: `Bid`.
  - Xây dựng API `POST /api/v1/auctions/{id}/bids`. Bên trong hàm Service (`@Transactional`), bắt buộc thực thi theo đúng trình tự khóa (**Pessimistic Write Lock** trên bản ghi `Auction` hoặc **Redis Distributed Lock**):
    1. Kiểm tra trạng thái phiên phải là `IN_PROGRESS` và thời gian hiện tại nhỏ hơn `end_time`.
    2. Chặn người bán tự đặt giá sản phẩm của chính mình (`seller_id != bidder_id`).
    3. Kiểm tra mức giá hợp lệ: $bid\_amount \ge current\_price + step\_price$ (hoặc $\ge starting\_price$ nếu là người mở hàng đầu tiên).
    4. Kiểm tra số dư khả dụng của người đặt: $user.balance \ge bid\_amount$.
    5. **Hoàn tiền khóa cho người dẫn đầu cũ (nếu có)**: Tìm lượt bid cao nhất liền trước, giảm `locked_balance`, cộng trả lại `balance` cho người đó và lưu bản ghi `WalletTransaction` loại `BID_UNLOCK`.
    6. **Khóa tiền người đặt mới**: Giảm `balance`, tăng `locked_balance` của người vừa đặt và lưu bản ghi `WalletTransaction` loại `BID_LOCK`.
    7. **Chống cướp cò phút chót (Anti-Sniping)**: Nếu $end\_time - now \le 30\text{ giây}$, tự động cộng thêm 60 giây vào `end_time`.
    8. Lưu bản ghi `Bid`, cập nhật `current_price` trong bảng `auctions`.
    9. Gửi thông báo WebSocket chứa giá mới, tên người dẫn đầu mới và `end_time` mới tới toàn bộ người xem trong phòng.
- [ ] **3. Kiểm thử Đa luồng (Bắt buộc thực hiện ngay trong nhánh `feat/bidding-engine`)**:
  - Viết Integration Test bằng JUnit sử dụng `ExecutorService` và `CountDownLatch` giả lập 30–50 luồng cùng bấm đặt giá vào đúng 1 mili-giây. Đảm bảo chỉ có các lệnh hợp lệ tuần tự đi qua, tổng tiền trong ví không bao giờ bị sai lệch.

---

### Giai đoạn 5: Tự động hóa Vòng đời & Tất toán (Scheduler & Settlement)
Tự động đóng/mở phiên và giải ngân tiền khi hết giờ mà không cần con người can thiệp.

- [ ] **1. Tiến trình chạy ngầm (`feat/auction-scheduler`)**:
  - Dùng `@Scheduled` quét định kỳ mỗi giây (hoặc dùng Redis Delayed Queue):
    - **Mở phiên**: Tìm các phiên `UPCOMING` có $start\_time \le now$, chuyển sang `IN_PROGRESS` và bắn WebSocket thông báo mở sàn.
    - **Đóng phiên & Tất toán**: Tìm các phiên `IN_PROGRESS` có $end\_time \le now$, khóa bản ghi lại và xử lý:
      - Nếu có người đặt giá: Gán `winner_id` cho người trả giá cao nhất. Trừ vĩnh viễn số tiền thắng cuộc khỏi `locked_balance` của người thắng (ghi log `AUCTION_PAYMENT`). Cộng số tiền đó vào `balance` của người bán `seller_id` (ghi log `SELLER_PAYOUT`).
      - Chuyển trạng thái phiên sang `COMPLETED` và phát sự kiện kết thúc qua WebSocket.

---

### Giai đoạn 6: Xây dựng Giao diện ReactJS (Frontend Development)
Triển khai trên thư mục `auction-frontend`, chia nhánh tương ứng với từng cụm màn hình:

- [ ] **1. `feat/fe-core-auth`**: Cấu hình `axios` (tự động gắn Bearer Token, xử lý hết hạn phiên), `react-router-dom`, `AuthContext`. Xây dựng trang Đăng nhập, Đăng ký và trang Quản lý Ví tiền (Nạp tiền, xem biểu đồ/bảng lịch sử `BID_LOCK`, `BID_UNLOCK`).
- [ ] **2. `feat/fe-marketplace`**: Trang chủ hiển thị danh sách phiên đấu giá, bộ lọc theo danh mục, thanh tìm kiếm và trạng thái phiên.
- [ ] **3. `feat/fe-auction-room`**: Trang chi tiết Phòng Đấu Giá. Tích hợp `@stomp/stompjs` kết nối vào `/topic/auctions/{id}`, làm đồng hồ đếm ngược tự động cộng thêm giờ khi có Anti-sniping, bảng lịch sử nhảy giá trực tiếp kèm hiệu ứng, và nút đặt giá nhanh.
- [ ] **4. `feat/fe-seller-portal`**: Giao diện dành cho Người bán để tải ảnh sản phẩm lên và tạo lịch mở phiên đấu giá.

---

## 6. Lệnh chạy & Phát triển thường dùng

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