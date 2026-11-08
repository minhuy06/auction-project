package com.auction.auctionbackend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionRequest (
        @NotNull(message = "ID sản phẩm không được để trống")
        Long productId,

        @NotNull(message = "Giá khởi điểm không được để trống")
        @DecimalMin(value = "0.01", message = "Giá khởi điểm phải lớn hơn 0")
        BigDecimal startingPrice,

        @NotNull(message = "Bước giá không được để trống")
        @DecimalMin(value = "0.01", message = "Bước giá phải lớn hơn 0")
        BigDecimal stepPrice,

        @NotNull(message = "Thời gian bắt đầu không được để trống")
        @Future(message = "Thời gian bắt đầu phải ở trong tương lai")
        LocalDateTime startTime,

        @NotNull(message = "Thời gian kết thúc không được để trống")
        @Future(message = "Thời gian kết thúc phải ở trong tương lai")
        LocalDateTime endTime
) {
}
