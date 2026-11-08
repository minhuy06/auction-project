package com.auction.auctionbackend.dto.response;

import com.auction.auctionbackend.entity.Auction;
import com.auction.auctionbackend.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionResponse (
        Long id,
        ProductResponse product,
        BigDecimal startingPrice,
        BigDecimal stepPrice,
        BigDecimal currentPrice,
        AuctionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long winnerId,
        String winnerUsername,
        LocalDateTime createdAt
) {
    public static AuctionResponse fromEntity(Auction auction){
        return new AuctionResponse(
                auction.getId(),
                ProductResponse.fromEntity(auction.getProduct()),
                auction.getStartingPrice(),
                auction.getStepPrice(),
                auction.getCurrentPrice(),
                auction.getStatus(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getWinner() != null ? auction.getWinner().getId() : null,
                auction.getWinner() != null ? auction.getWinner().getUsername() : null,
                auction.getCreatedAt()
        );
    }
}