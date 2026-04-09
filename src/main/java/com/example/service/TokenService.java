package com.example.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.model.Token;
import com.example.model.User;
import com.example.repository.TokenRepository;

@Service
public class TokenService {
	
	 
	@Autowired
	private TokenRepository tokenRepository;
	 
	 @Autowired 
	 private EmailService emailService; 
	 
	 @Autowired
	 private JdbcTemplate jdbcTemplate; 
	 
	 public Map<String, Object> generateToken(String email) 
	 {
		  Map<String, Object> map = new HashMap<>();
		  String sql = "SELECT * FROM users WHERE email = ?";   
		  List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email);  
		  if (sessionRows.size() != 0) 
		  { 
			  Long id = (Long)sessionRows.get(0).get("id"); 
			  sql = "SELECT * FROM token WHERE user_id = ?";   
			  sessionRows = jdbcTemplate.queryForList(sql, id);  
			  if (sessionRows.size() < 3) 
			  {
				  Token token = new Token();
			      token.setToken(UUID.randomUUID().toString());
			      token.setCAt(LocalDateTime.now());
			      token.setExpTime(LocalDateTime.now().plusMinutes(60)); 
			      User user = new User(); 
			      user.setId(id); 
			      token.setUser(user);
			      boolean ok = emailService.send(email, "http://localhost:4200/reset?token=" + token.getToken()); 
			      if (ok)
			      {
			    	  tokenRepository.save(token);
			    	  map.put("reponse", true); 
			      }
			      else 
			    	  map.put("reponse", "transmission"); 
			  }
			  else 
				  map.put("reponse", "limite");
		  }
		  else 
			  map.put("reponse", "email"); 
		  return map; 
	 }
	
}
