package com.auction.auctionbackend.exception;

public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    // Constructor dùng thông báo mặc định của ErrorCode
    public AppException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    // Constructor ghi đè thông báo động
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}