package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.entity.Role;
import com.auction.auctionbackend.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
