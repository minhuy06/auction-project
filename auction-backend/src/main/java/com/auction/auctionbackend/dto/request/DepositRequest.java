package com.auction.auctionbackend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepositRequest (

        @NotNull(message = "Số tiền nạp không được để trống")
        @DecimalMin(value = "10000.00", message = "Số tiền nạp tối thiểu là 10,000 VNĐ")
        @Digits(integer = 13, fraction = 2, message = "Số tiền không hợp lệ (tối đa 13 chữ số phần nguyên và 2 chữ số thập phân)")
        BigDecimal amount,

        @Size(max = 255, message = "Nội dung giao dịch không được vượt quá 255 ký tự")
        String description
) {
}
