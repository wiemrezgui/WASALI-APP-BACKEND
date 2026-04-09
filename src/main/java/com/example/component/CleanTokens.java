package com.example.component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.model.Token;
import com.example.repository.TokenRepository;

@Component 
public class CleanTokens {
	
	 @Autowired
	 private TokenRepository tokenRepository;
	 
	 @Autowired
	 private JdbcTemplate jdbcTemplate; 

	 @Scheduled(fixedRate = 3600000)
	 public void cleanExpTokens() 
	 {
		 
	    LocalDateTime nw = LocalDateTime.now(); 
	    String sql = "SELECT id FROM token WHERE exp_time <= ?";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, nw);
		for(Map<String, Object> row : sessionRows)
		{
			Token tk = new Token(); 
			tk.setId((long)row.get("id"));
			tokenRepository.delete(tk);
		}
	 }
}
