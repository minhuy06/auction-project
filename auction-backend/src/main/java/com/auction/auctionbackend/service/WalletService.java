package com.auction.auctionbackend.service;

import com.auction.auctionbackend.dto.request.DepositRequest;
import com.auction.auctionbackend.dto.request.WithdrawRequest;
import com.auction.auctionbackend.dto.response.WalletTransactionResponse;
import com.auction.auctionbackend.entity.User;
import com.auction.auctionbackend.entity.WalletTransaction;
import com.auction.auctionbackend.enums.TransactionStatus;
import com.auction.auctionbackend.enums.TransactionType;
import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.auction.auctionbackend.repository.UserRepository;
import com.auction.auctionbackend.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public WalletTransactionResponse deposit(String username, DepositRequest request){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setBalance(user.getBalance().add(request.amount()));
        userRepository.save(user);

        // Nội dung mô tả mặc định
        String description = (request.description() != null && !request.description().isBlank()) ? request.description() : "Nạp tiền vào ví";

        // ghi log giao dịch DEPOSI
        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .amount(request.amount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();

        WalletTransaction savedTx = walletTransactionRepository.save(transaction);
        return WalletTransactionResponse.fromEntity(savedTx);
    }

    @Transactional
    public WalletTransactionResponse withdraw(String username, WithdrawRequest request){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setBalance(user.getBalance().subtract(request.amount()));
        userRepository.save(user);

        String description = (request.description() != null && !request.description().isBlank())
                ? request.description()
                : "Rút tiền từ ví";

        // Ghi log giao dịch WITHDRAW
        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .amount(request.amount())
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();

        WalletTransaction savedTx = walletTransactionRepository.save(transaction);
        return WalletTransactionResponse.fromEntity(savedTx);
    }

    // xem lịch sử biến động số dư có phân trang
    @Transactional(readOnly = true)
    public Page<WalletTransactionResponse> getMyTransaction(String username, int page, int size){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        return walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable).map(WalletTransactionResponse::fromEntity);
    }
}
