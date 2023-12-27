package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    private UserRepository userRepository;
    public static final String SECRET = "404D635166546A576E5A7234753778214125442A472D4B6150645267556B5870";

//    public static final int accessTokenExpirationTime = Integer.parseInt(System.getenv("JWT_ACCESS_TOKEN_EXPIRATION_TIME"));
    //Variable for Test
    public static final int accessTokenExpirationTime = 1000*60*5;
//    public static final int refreshTokenExpirationTime = Integer.parseInt(System.getenv("JWT_REFRESH_TOKEN_EXPIRATION_TIME"));
    //Variable for Test
    public static final int refreshTokenExpirationTime = 1000*60*10;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getBannedUntil() != null && LocalDateTime.now().isBefore(user.getBannedUntil())) {
                return false;
            }
        }

        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String refreshToken, User user) {
        final String username = extractUsername(refreshToken);
        return (username.equals(user.getUsername())
                && !isTokenExpired(refreshToken));
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder().setClaims(claims).setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createRefreshToken(claims, userName);
    }

    private String createRefreshToken(Map<String, Object> claims, String userName) {
        return Jwts.builder().setClaims(claims).setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
