package com.glc.smartcar.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    private long expirationTime;
    private String jwtKey;

    public TokenProvider(@Value("${jwt.expiration}") long expirationTime, @Value("${jwt.key}") String jwt_key) {
        this.expirationTime = expirationTime;
        this.jwtKey = jwt_key;
    }


    public String gerarToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return construirToken(user.getUsername());
    }

    private String construirToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getChaveSecreta())
                .compact();
    }

    private SecretKey getChaveSecreta(){
        return Keys.hmacShaKeyFor(jwtKey.getBytes());
    }

    public boolean validarToken(String token){
        try {
            getClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getChaveSecreta())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token){
        return getClaims(token).getSubject();
    }
}
