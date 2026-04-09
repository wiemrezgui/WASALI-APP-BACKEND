package com.example.component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    
	private final String jwtSecret = "ccv36zwc434spf0x7s4kucgb2w9straidcrparn2yhdbkwrhzle4vwkmac1400om"; 
	
  
	private final long jwtExpInMs = 3600000; 
	

	private Key getSgKey() {
        byte[] keyBs = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBs);
    }
	
	
	public String generateTk(Authentication authentication) {
        Date nw = new Date();
        Date expiryDt = new Date(nw.getTime() + jwtExpInMs);
        
       
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(expiryDt)
                .signWith(getSgKey(), SignatureAlgorithm.HS512)
                .compact();
    }
	
	
	public String getEmail(String token) {

		Claims claims = Jwts.parserBuilder()
        	    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
        	    .build()
        	    .parseClaimsJws(token)
        	    .getBody(); 
		
		
    	return claims.getSubject(); 
    }
	
	
	
	
	
	
	public boolean validateTk(String token) { 
		 try 
		 {
			 @SuppressWarnings("unused")
			final Claims claims = Jwts.parserBuilder()
		         		.setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
		                .build()
		                .parseClaimsJws(token)
		         	    .getBody(); 
		 }
		 catch(Exception e) 
		 {
			 return false; 
		 }
		 return true; 
    }
}

