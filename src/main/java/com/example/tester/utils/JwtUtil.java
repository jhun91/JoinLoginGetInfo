package com.example.tester.utils;

import com.example.tester.domain.member.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtUtil {

    //@Value("${custom-jwt.key}")
    private String secret = "12345678901234567890123456789012";

    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String userId, String name) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("userName", name)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
