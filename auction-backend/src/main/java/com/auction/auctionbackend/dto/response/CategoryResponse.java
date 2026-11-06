package com.auction.auctionbackend.dto.response;

import com.auction.auctionbackend.entity.Category;

import java.util.List;

public record CategoryResponse (
        Long id,
        String name,
        String description,
        Long parentId,
        List<CategoryResponse> children
) {
    public static CategoryResponse fromEntity(Category category){
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getChildren() != null ? category.getChildren().stream().map(CategoryResponse::fromEntity).toList() : List.of()
        );
    }
}
