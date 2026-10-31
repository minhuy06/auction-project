package com.auction.auctionbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T> (
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>(200, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(200, "Thành công", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(T data, String message){
        return new ApiResponse<>(201, message, data, LocalDateTime.now());
    }
}
