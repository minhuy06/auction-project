package com.auction.auctionbackend.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Lỗi Hệ thống & Đầu vào chung
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001", "Lỗi hệ thống nội bộ, vui lòng thử lại sau"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VAL_001", "Dữ liệu đầu vào không hợp lệ"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "Không tìm thấy tài nguyên yêu cầu"),

    // Lỗi Xác thực & Phân quyền
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_001", "Tên đăng nhập hoặc email đã tồn tại"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_002", "Tên đăng nhập hoặc mật khẩu không chính xác"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_003", "Bạn cần đăng nhập để thực hiện thao tác này"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_004", "Bạn không có quyền truy cập tài nguyên này"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_005", "Tài khoản của bạn đã bị khóa"),

    // Lỗi Ví tiền & Đấu giá
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_001", "Số dư khả dụng trong ví không đủ"),
    AUCTION_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "AUC_001", "Phiên đấu giá chưa bắt đầu hoặc đã kết thúc"),
    INVALID_BID_AMOUNT(HttpStatus.BAD_REQUEST, "AUC_002", "Mức giá đặt phải lớn hơn hoặc bằng giá hiện tại cộng bước giá"),
    SELLER_CANNOT_BID(HttpStatus.FORBIDDEN, "AUC_003", "Người bán không được phép tự đặt giá sản phẩm của chính mình"),
    BID_CONFLICT(HttpStatus.CONFLICT, "AUC_004", "Đã có người khác đặt mức giá cao hơn, vui lòng thử lại");

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