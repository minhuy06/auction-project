package com.auction.auctionbackend.event;

import com.auction.auctionbackend.dto.response.AuctionUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuctionWebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlacedEvent(BidPlacedEvent event){
        AuctionUpdateMessage payload = event.payload();
        messagingTemplate.convertAndSend("/topic/auctions/" + payload.auctionId(), payload);
    }
}
