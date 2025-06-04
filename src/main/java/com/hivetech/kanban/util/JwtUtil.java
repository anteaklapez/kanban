package com.hivetech.kanban.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long expiration;
    private SecretKey key;

    // Initializes the key after the class is instantiated and the jwtSecret is injected,
    // preventing the repeated creation of the key and enhancing performance
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Generates jwt token from provided username, secret key,
     * and expiration date.
     * @param username the username of existing user
     * @return jwt token as {@code String}
     */
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from the jwt token sent by a user.
     * @param token the jwt token
     * @return String claim extracted from the jwt token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // subject should be username
    }

    /**
     * Generic method for extracting a specific claim.
     * @param token the jwt token
     * @param claimsResolver processes appropriate claim
     * @return T object after processing
     * @param <T> claim to be processed
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the given jwt token based on the given user credentials.
     * @param token the jwt token to validate
     * @param username user credentials to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, String username){
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    /**
     * Evaluates whether the given token is expired or not.
     * @param token the token to evaluate
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract the expiration date from the token
     * @param token the token to extract the date from
     * @return Date of expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the claims from the token.
     *
     * @param token the token to extract the claims from.
     * @return Claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
