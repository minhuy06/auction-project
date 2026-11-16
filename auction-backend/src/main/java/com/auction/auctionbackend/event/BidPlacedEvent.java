package com.auction.auctionbackend.event;

import com.auction.auctionbackend.dto.response.AuctionUpdateMessage;

public record BidPlacedEvent(AuctionUpdateMessage payload) {
}
