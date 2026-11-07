package com.auction.auctionbackend.dto.response;

import com.auction.auctionbackend.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse (
        Long id,
        Long sellerId,
        String sellerUsername,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static ProductResponse fromEntity(Product product){
        return new ProductResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getSeller().getUsername(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getName(),
                product.getDescription(),
                product.getImageUrls() != null ? product.getImageUrls() : List.of(),
                product.getCreatedAt()
        );
    }
}
