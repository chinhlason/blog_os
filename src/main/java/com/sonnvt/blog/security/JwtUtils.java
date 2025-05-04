package com.sonnvt.blog.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {
    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration_time}")
    private long EXPIRATION_TIME;

    @Value("${jwt.rf_secret_key}")
    private String RF_SECRET_KEY;

    @Value("${jwt.rf_expiration_time}")
    private long RF_EXPIRATION_TIME;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private SecretKey getRfSecretKey() {
        return Keys.hmacShaKeyFor(RF_SECRET_KEY.getBytes());
    }

    public String generateToken(String username, Long userId, String roles) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String username, Long userId, String roles) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + RF_EXPIRATION_TIME))
                .signWith(getRfSecretKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims extractRfClaims(String token) {
        return Jwts.parser()
                .verifyWith(getRfSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private <T> T extractRfClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractRfClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRfUsername(String token) {
        return extractRfClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public Long extractRfUserId(String token) {
        return extractRfClaims(token).get("userId", Long.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractRfExpiration(String token) {
        return extractRfClaim(token, Claims::getExpiration);
    }

    public String extractRoles(String token) {
        return extractClaims(token).get("roles", String.class);
    }

    public String extractRfRoles(String token) {
        return extractRfClaims(token).get("roles", String.class);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parse(token);
            return true;
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

    public boolean isValidRfToken(String token) {
        try {
            Jwts.parser().verifyWith(getRfSecretKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Rf token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT Rf token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT Rf token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT Rf token claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
