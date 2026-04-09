package com.example.controller;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.repository.RoleRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.example.model.Role; 


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	 @Autowired
	 private AuthenticationManager authenticationManager; 
	 
	 @Autowired
	 private JwtTokenProvider jwtTokenProvider;
	 
	 @Autowired
	 private JdbcTemplate jdbcTemplate; 
	 
	 @Autowired 
	 private RoleRepository roleRepository; 
	 
	 private final String jwtSecret = "ccv36zwc434spf0x7s4kucgb2w9straidcrparn2yhdbkwrhzle4vwkmac1400om"; 
		 
	 private final long jwtExpInMs = 3600000;
	 
	 
	 
	 @PostMapping("login") 
	 public Map<String, String> authenticateUser(@RequestPart("email") String email, @RequestPart("password") String password) { 
		
		 Map<String, String> map = new HashMap<>(); 
		 try
		 {
			  Authentication authentication = authenticationManager.authenticate(
		              new UsernamePasswordAuthenticationToken(email, password)); 
			  String sql = "SELECT * FROM users WHERE email = ?";   
			  List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email);
			  Optional<Role> role = roleRepository.findById((Long)sessionRows.get(0).get("role_id"));
			  if (role.get().getRole().equals("Livreur")) 
				if (((boolean)sessionRows.get(0).get("activation"))) 
				{
						  SecurityContextHolder.getContext().setAuthentication(authentication); 
					      String jwt = jwtTokenProvider.generateTk(authentication); 
					      map.put("token", jwt); 
				}
				else 
						  map.put("reponse", "validation");  
		    else 
			{
					SecurityContextHolder.getContext().setAuthentication(authentication); 
				    String jwt = jwtTokenProvider.generateTk(authentication); 
				    map.put("token", jwt); 
		    }
		 }
		 catch (BadCredentialsException e) 
		 {
			 map.put("reponse", "invalide");  
		 }
		 return map; 
	 }
	 
	 
	 @PostMapping("generate") 
	 public Map<String, String> getToken(@RequestPart("email") String email) 
	 {
		 Date nw = new Date();
	     Date expiryDt = new Date(nw.getTime() + jwtExpInMs);
	       
	    String token =  Jwts.builder()
	                .setSubject(email)
	                .setIssuedAt(new Date())
	                .setExpiration(expiryDt)
	                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
	                .compact(); 
	    Map<String, String> m = new HashMap<>(); 
	    m.put("token", token); 
	    return m; 
	 }
	 
	 
	
	
}
