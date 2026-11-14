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
import com.auction.auctionbackend.repository.specification.AuctionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("endTime", "startTime", "currentPrice", "startingPrice", "createdAt");

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

    @Transactional(readOnly = true)
    public Page<AuctionResponse> searchAuctions(
            String keyword,
            Long categoryId,
            AuctionStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        String validSortBy = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "endTime");
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(validSortBy).descending()
                : Sort.by(validSortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Auction> spec = AuctionSpecification.filterAuctions(keyword, categoryId, status, minPrice, maxPrice);
        return auctionRepository.findAll(spec, pageable)
                .map(AuctionResponse::fromEntity);
    }
}