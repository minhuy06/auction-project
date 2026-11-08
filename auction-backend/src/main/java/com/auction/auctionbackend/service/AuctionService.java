package com.auction.auctionbackend.service;

import com.auction.auctionbackend.dto.request.AuctionRequest;
import com.auction.auctionbackend.dto.response.AuctionResponse;
import com.auction.auctionbackend.entity.Auction;
import com.auction.auctionbackend.entity.Product;
import com.auction.auctionbackend.enums.AuctionStatus;
import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public AuctionResponse createAuction(String username, AuctionRequest request){
        LocalDateTime now = LocalDateTime.now();
        if(!request.startTime().isAfter(now) || !request.endTime().isAfter(request.startTime())){
            throw new AppException(ErrorCode.INVALID_AUCTION_TIME);
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if(!product.getSeller().getUsername().equals(username)){
            throw new AppException(ErrorCode.NOT_PRODUCT_OWNER);
        }
        if (auctionRepository.existsByProductId(product.getId())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_IN_AUCTION);
        }

        Auction auction = Auction.builder()
                .product(product)
                .startingPrice(request.startingPrice())
                .stepPrice(request.stepPrice())
                .currentPrice(request.startingPrice())
                .status(AuctionStatus.UPCOMING)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        Auction savedAuction = auctionRepository.save(auction);
        return AuctionResponse.fromEntity(savedAuction);
    }

    @Transactional(readOnly = true)
    public AuctionResponse getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));
        return AuctionResponse.fromEntity(auction);
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponse> getMyAuctions(String username, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return auctionRepository.findByProductSellerUsernameOrderByCreatedAtDesc(username, pageable)
                .map(AuctionResponse::fromEntity);
    }
}