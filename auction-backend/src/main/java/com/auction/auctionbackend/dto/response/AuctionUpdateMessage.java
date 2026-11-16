package com.auction.auctionbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionUpdateMessage (
        Long auctionId,
        BigDecimal currentPrice,
        String highestBidderUsername,
        LocalDateTime endTime,
        LocalDateTime bidTime,
        boolean timeExtended
) { }