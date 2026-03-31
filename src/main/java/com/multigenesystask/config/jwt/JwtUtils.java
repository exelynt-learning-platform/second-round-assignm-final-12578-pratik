package com.multigenesystask.config.jwt;


import io.jsonwebtoken.ExpiredJwtException;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.multigenesystask.service.CustomUserDetails;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.stream.Collectors;
@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationsMs;

    @PostConstruct
    public void validateJwtSecret() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException(
                    "JWT secret is too short. Minimum 32 characters required for HMAC-SHA256 security.");
        }
        // Key rotation note: In production, rotate via environment variable redeployment
        // and maintain a list of accepted keys during rotation window.
    }
    //Authorization -> Bearer <TOKEN>
    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(CustomUserDetails userDetails){
        String email = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationsMs)))

                .signWith(
                        key())
                .compact();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken){
        try {
            Jwts.parser().verifyWith(key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());

        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());

        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());

        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
