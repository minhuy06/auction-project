package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"seller", "category"})
    Page<Product> findBySellerUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"seller", "category"})
    Optional<Product> findById(Long id);
}
