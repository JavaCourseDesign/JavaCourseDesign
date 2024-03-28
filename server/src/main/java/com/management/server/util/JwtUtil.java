package com.management.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    static private String secret = "your_secret_key_your_secret_key"; // 密钥至少需要256位
    static private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    static public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 设置过期时间
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = extractUsername(token);
        return (username.equals(usernameFromToken) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}
