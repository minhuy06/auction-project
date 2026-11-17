package com.auction.auctionbackend.event;

import com.auction.auctionbackend.dto.response.AuctionUpdateMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionWebSocketEventListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AuctionWebSocketEventListener eventListener;

    @Test
    @DisplayName("1. Khi nhận BidPlacedEvent, hệ thống phải bắn đúng payload tới kênh /topic/auctions/{auctionId}")
    void shouldSendWebSocketMessageToCorrectAuctionTopic(){
        Long auctionId = 99L;
        BigDecimal newPrice = new BigDecimal("55000000.00");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(10);

        AuctionUpdateMessage message = new AuctionUpdateMessage(
                auctionId,
                newPrice,
                "bidder_vip_01",
                endTime,
                now,
                true
        );

        BidPlacedEvent event = new BidPlacedEvent(message);
        eventListener.handleBidPlacedEvent(event);

        ArgumentCaptor<AuctionUpdateMessage> payloadCaptor = ArgumentCaptor.forClass(AuctionUpdateMessage.class);
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/auctions/99"), payloadCaptor.capture());

        AuctionUpdateMessage sentPayload = payloadCaptor.getValue();
        assertEquals(auctionId, sentPayload.auctionId());
        assertEquals(newPrice, sentPayload.currentPrice());
        assertEquals("bidder_vip_01", sentPayload.highestBidderUsername());
        assertEquals(endTime, sentPayload.endTime());
        assertTrue(sentPayload.timeExtended());
    }
}
