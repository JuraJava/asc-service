package com.yurdan.ascService.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.JwtAuthenticationDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public JwtAuthentication getAuthentication(String accessToken) {
        String[] parts = accessToken.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid token");

        String payloadJson = new String(Base64.getDecoder().decode(parts[1]));
        try {
            JwtAuthenticationDto requestDto = new ObjectMapper().readValue(payloadJson, JwtAuthenticationDto.class);

            if (requestDto == null) {
                throw new RuntimeException("Request data is empty");
            }

            if (requestDto.userId() == null) {
                throw new RuntimeException("User is empty");
            }

            return new JwtAuthentication(
                    requestDto.userId(),
                    requestDto.roles().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
        } catch(Exception e) {
            throw new RuntimeException("Invalid token payload", e);
        }
    }
}
