package com.afci.training.planning.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretBase64;

    @Value("${app.jwt.exp-minutes:120}")
    private long expMinutes;

    private Key key() {
        byte[] raw = Base64.getDecoder().decode(secretBase64);
        return Keys.hmacShaKeyFor(raw);
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(key()).build();
        return parser.parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String token, UserDetails user) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(key()).build();
        var claims = parser.parseClaimsJws(token).getBody();
        return user.getUsername().equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    // ====== AJOUTS pour la vérification d'email ======

    /** Génère un JWT arbitraire avec des claims custom et une expiration en minutes. */
    public String generateTokenWithClaims(Map<String, Object> claims, int expMinutesCustom) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expMinutesCustom, ChronoUnit.MINUTES)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Parse et retourne les claims d’un token arbitraire. */
    public Claims parseClaims(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(key()).build();
        return parser.parseClaimsJws(token).getBody();
    }
}
