package com.auction.auctionbackend.repository.specification;

import com.auction.auctionbackend.entity.Auction;
import com.auction.auctionbackend.entity.Product;
import com.auction.auctionbackend.enums.AuctionStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AuctionSpecification {
    public static Specification<Auction> filterAuctions(
        String keyword,
        Long categoryId,
        AuctionStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            if(query != null && query.getResultType() != Long.class && query.getResultType() != long.class){
                var productFetch = root.fetch("product", JoinType.INNER);
                productFetch.fetch("seller", JoinType.INNER);
                productFetch.fetch("category", JoinType.INNER);
                root.fetch("winner", JoinType.INNER);
            }

            List<Predicate> predicats = new ArrayList<>();
            Join<Auction, Product> productJoin = root.join("product", JoinType.INNER);

            if(keyword != null && !keyword.isBlank()){
                predicats.add(criteriaBuilder.like(
                        criteriaBuilder.lower(productJoin.get("name")),
                        "%" + keyword.trim().toLowerCase() + "%"
                ));
            }

            if(categoryId != null){
                predicats.add(criteriaBuilder.equal(
                        productJoin.get("category").get("id"),
                        categoryId
                ));
            }

            if(status != null){
                predicats.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if(minPrice != null){
                predicats.add(criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), minPrice));
            }

            if(maxPrice != null){
                predicats.add(criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), maxPrice));
            }

            return criteriaBuilder.and(predicats.toArray(new Predicate[0]));
        };
    }
}
