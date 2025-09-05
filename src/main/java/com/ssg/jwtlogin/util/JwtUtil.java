package com.ssg.jwtlogin.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtUtil {

    @Value("${jwt.secret}") // application.properties에 들어있는 key 가져오기
    private String secretKey;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration; // 유효기간

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;


    // AccessToken 발급
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpiration);
    }

    // RefreshToken 발급
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration);
    }

    /**
     * JJWT의 Token 생성
     */
    private String generateToken(String username, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) // 이 토큰의 발급 시점
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()),SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증하기
     */
    public boolean validateToken(String token) {
        // 1. 토큰이 일치하는지 검증
        // 2. 유효기간이 만료되었는지 검증
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch ( JwtException e) {
            return false;
        }
    }

    /**
     *  회원 아이디 꺼내기
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
