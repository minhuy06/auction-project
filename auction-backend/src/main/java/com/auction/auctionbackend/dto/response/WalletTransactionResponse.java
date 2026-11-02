package com.auction.auctionbackend.dto.response;

import com.auction.auctionbackend.entity.WalletTransaction;
import com.auction.auctionbackend.enums.TransactionStatus;
import com.auction.auctionbackend.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse (
        Long id,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        String description,
        Long auctionId,
        LocalDateTime createdAt
        )
{
    public static WalletTransactionResponse fromEntity(WalletTransaction tx){
        return new WalletTransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getStatus(),
                tx.getDescription(),
                tx.getAuctionId(),
                tx.getCreatedAt()
        );
    }
}
