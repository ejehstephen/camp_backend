package com.campnest.campnest_backend.dto;

import com.campnest.campnest_backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 🔐 Strong secret key (use env var or config file in production)
    private static final String SECRET = "mysupersecureandlongsecretkey1234567890";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Generate JWT with user ID as subject (UUID) and email claim
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) // store UUID as subject
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract subject (UUID) from token
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract email claim
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // ✅ Generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Validate token by expiration only (we trust ID & email in claims)
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}
