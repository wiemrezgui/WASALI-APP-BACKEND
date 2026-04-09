package com.example.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 

import com.example.service.TokenService;

@RestController
@RequestMapping("/api/token")
public class TokenController {
	

	
	@Autowired 
	private TokenService tokenService; 
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	
	@PostMapping("save")
	public Map<String, Object> createToken(@RequestBody String email) 
	{
	
		Map<String, Object> map = tokenService.generateToken(email);  
		
		return map; 
	}
	
	@PostMapping("verify") 
	public boolean verifyToken(@RequestBody String token) 
	{  
		String sql = "SELECT * FROM token WHERE token = ?";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, token);
        if (sessionRows.size() != 0 && ((Timestamp) sessionRows.get(0).get("exp_time")).toLocalDateTime().isAfter(LocalDateTime.now()) )
            return true;     
        else 
        	return false;   
	}
	
	@PostMapping("email") 
	public Map<String, Object> getEmail(@RequestBody String token) 
	{
		Map<String, Object> m = new HashMap<>(); 
		String sql = "SELECT * FROM token , users u WHERE token = ? and u.id = user_id";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, token);
		if (sessionRows.size() != 0) 
		{
			m.put("email", sessionRows.get(0).get("email"));   
		}
		else 
		{
			m.put("email", false);  
		}
		return m; 
	}

	
}
	
