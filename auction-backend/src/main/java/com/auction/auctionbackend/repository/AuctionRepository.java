package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {
    boolean existsByProductId(Long productId);
    Page<Auction> findByProductSellerUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}