package com.auction.auctionbackend.entity;

import com.auction.auctionbackend.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "starting_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "step_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal stepPrice;

    @Column(name = "current_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.UPCOMING;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
