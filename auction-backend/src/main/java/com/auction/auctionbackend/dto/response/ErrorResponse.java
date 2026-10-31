package com.auction.auctionbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String errorCode,
        String message,
        String path,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {

    // Constructor dùng cho các lỗi nghiệp vụ thông thường (không có fieldErrors)
    public ErrorResponse(int status, String errorCode, String message, String path) {
        this(status, errorCode, message, path, null, LocalDateTime.now());
    }

    public ErrorResponse(int status, String errorCode, String message, String path, Map<String, String> fieldErrors) {
        this(status, errorCode, message, path, fieldErrors, LocalDateTime.now());
    }
}