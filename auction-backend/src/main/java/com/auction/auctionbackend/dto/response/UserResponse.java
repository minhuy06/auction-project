package com.auction.auctionbackend.dto.response;

import com.auction.auctionbackend.entity.User;
import com.auction.auctionbackend.enums.UserStatus;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse (
        Long id,
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        UserStatus status,
        BigDecimal balance,
        BigDecimal lockedBalance,
        Set<String> roles
) {

    public static UserResponse fromEntity(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.getStatus(),
                user.getBalance(),
                user.getLockedBalance(),
                user.getRoles().stream().map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
    }
}
