package com.auction.auctionbackend.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Lỗi Hệ thống & Đầu vào chung
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001", "Lỗi hệ thống nội bộ, vui lòng thử lại sau"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VAL_001", "Dữ liệu đầu vào không hợp lệ"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "Không tìm thấy tài nguyên yêu cầu"),

    // Lỗi Xác thực & Người dùng
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_001", "Tên đăng nhập đã tồn tại"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_002", "Email này đã được sử dụng"),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_003", "Số điện thoại này đã được sử dụng"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_004", "Tên đăng nhập hoặc mật khẩu không chính xác"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_005", "Tài khoản của bạn đã bị khóa hoặc vô hiệu hóa"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_006", "Vui lòng đăng nhập để tiếp tục"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_007", "Bạn không có quyền truy cập tài nguyên này"),
    ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_001", "Lỗi hệ thống nội bộ, vui lòng thử lại sau"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "Không tìm thấy người dùng"),

    // Lỗi Ví tiền & Đấu giá
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_001", "Số dư khả dụng trong ví không đủ"),
    AUCTION_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "AUC_001", "Phiên đấu giá chưa bắt đầu hoặc đã kết thúc"),
    INVALID_BID_AMOUNT(HttpStatus.BAD_REQUEST, "AUC_002", "Mức giá đặt phải lớn hơn hoặc bằng giá hiện tại cộng bước giá"),
    SELLER_CANNOT_BID(HttpStatus.FORBIDDEN, "AUC_003", "Người bán không được phép tự đặt giá sản phẩm của chính mình"),
    BID_CONFLICT(HttpStatus.CONFLICT, "AUC_004", "Đã có người khác đặt mức giá cao hơn, vui lòng thử lại"),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "AUC_005", "Không tìm thấy phiên đấu giá"),
    PRODUCT_ALREADY_IN_AUCTION(HttpStatus.CONFLICT, "AUC_006", "Sản phẩm này đã được đăng ký trong một phiên đấu giá khác"),
    INVALID_AUCTION_TIME(HttpStatus.BAD_REQUEST, "AUC_007", "Thời gian kết thúc phải lớn hơn thời gian bắt đầu và thời gian bắt đầu phải ở tương lai"),

    // Lỗi danh mục & sản phẩm
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CAT_001", "Không tìm thấy danh mục"),
    CATEGORY_NAME_EXISTS(HttpStatus.CONFLICT, "CAT_002", "Tên danh mục đã tồn tại"),
    INVALID_CATEGORY_PARENT(HttpStatus.BAD_REQUEST, "CAT_003", "Danh mục cha không hợp lệ"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROD_001", "Không tìm thấy sản phẩm"),
    NOT_PRODUCT_OWNER(HttpStatus.FORBIDDEN, "PROD_002", "Bạn không có quyền chỉnh sửa sản phẩm của người khác"),
    ALREADY_SELLER(HttpStatus.CONFLICT, "USER_002", "Tài khoản của bạn đã có quyền người bán"),
    PRODUCT_IN_AUCTION(HttpStatus.CONFLICT, "PROD_003", "Không thể xóa sản phẩm đã được đưa vào phiên đấu giá"),

    // Lỗi tải file (Cloudinary)
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_001", "Định dạng file không hợp lệ, chỉ chấp nhận file ảnh"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_002", "Tải ảnh lên đám mây thất bại");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}