package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.request.DepositRequest;
import com.auction.auctionbackend.dto.request.WithdrawRequest;
import com.auction.auctionbackend.dto.response.ApiResponse;
import com.auction.auctionbackend.dto.response.WalletTransactionResponse;
import com.auction.auctionbackend.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<WalletTransactionResponse>> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DepositRequest request
            ) {

        WalletTransactionResponse response = walletService.deposit(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Nạp tiền vào ví thành công"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<WalletTransactionResponse>> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WithdrawRequest request
    ) {

        WalletTransactionResponse response = walletService.withdraw(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Rút tiền thành công"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<WalletTransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<WalletTransactionResponse> response = walletService.getMyTransaction(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy lịch sử giao dịch thành công"));
    }
}
