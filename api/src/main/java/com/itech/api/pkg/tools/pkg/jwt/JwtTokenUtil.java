package com.itech.api.pkg.tools.pkg.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itech.api.persistence.dao.UserDAO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1363985035870808959L;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private Long validity;

    public String getUsernameByToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getTokenExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDAO userDao) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userDao.getType());
        return doGenerateToken(claims, userDao.getUsername());
    }

  //validate token
    public Boolean validateToken(String token, UserDAO user) {
        final String username = getUsernameByToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
    
    public String refreshToken(String token) {
        Map<String, Object> claims = this.getAllClaimsFromToken(token);
        return (claims == null) ? null : doGenerateToken(claims, this.getUsernameByToken(token));
    }
    
    @SuppressWarnings("deprecation")
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        Date expDate = this.getTokenExpirationDate(token);
        return expDate.before(new Date());
    }

    @SuppressWarnings("deprecation")
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
}
