package com.auction.auctionbackend.service;

import com.auction.auctionbackend.dto.request.LoginRequest;
import com.auction.auctionbackend.dto.request.RegisterRequest;
import com.auction.auctionbackend.dto.response.AuthResponse;
import com.auction.auctionbackend.dto.response.UserResponse;
import com.auction.auctionbackend.entity.Role;
import com.auction.auctionbackend.entity.User;
import com.auction.auctionbackend.enums.RoleName;
import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.auction.auctionbackend.repository.RoleRepository;
import com.auction.auctionbackend.repository.UserRepository;
import com.auction.auctionbackend.security.CustomUserDetailsService;
import com.auction.auctionbackend.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.username())){
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (request.phone() != null && !request.phone().isBlank() && userRepository.existsByPhone(request.phone())) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .fullName(request.fullName())
                .phone(request.phone())
                .address(request.address())
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(newUser);
        return UserResponse.fromEntity(savedUser);
    }

    public AuthResponse login(LoginRequest request){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException ex){
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (LockedException | DisabledException ex){
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken, UserResponse.fromEntity(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.fromEntity(user);
    }
}
