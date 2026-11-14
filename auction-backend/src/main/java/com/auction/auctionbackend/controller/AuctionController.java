package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.request.AuctionRequest;
import com.auction.auctionbackend.dto.response.ApiResponse;
import com.auction.auctionbackend.dto.response.AuctionResponse;
import com.auction.auctionbackend.enums.AuctionStatus;
import com.auction.auctionbackend.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuctionResponse>> createAuction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AuctionRequest request
            ){
        AuctionResponse response = auctionService.createAuction(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response, "Tạo phiên đấu giá thành công"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<AuctionResponse>>> getMyAuctions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<AuctionResponse> responses = auctionService.getMyAuctions(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(responses, "Lấy danh sách phiên đấu giá của bạn thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuctionResponse>> getAuctionById(@PathVariable Long id) {
        AuctionResponse response = auctionService.getAuctionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết phiên đấu giá thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuctionResponse>>> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) AuctionStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "endTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<AuctionResponse> responses = auctionService.searchAuctions(keyword, categoryId, status, minPrice, maxPrice, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(responses, "Tìm kiếm phiên đấu giá thành công"));
    }
}
