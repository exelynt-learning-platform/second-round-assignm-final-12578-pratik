package com.multigenesystask.service;

import org.springframework.stereotype.Service;
import com.multigenesystask.config.jwt.JwtUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtUtils jwtUtils;

    public String generateToken(CustomUserDetails userDetails) {
        return jwtUtils.generateToken(userDetails);
    }
}