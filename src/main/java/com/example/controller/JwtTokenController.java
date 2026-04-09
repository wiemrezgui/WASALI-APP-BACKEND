package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;

@RestController
@RequestMapping("/api/jwt")
public class JwtTokenController {

	@Autowired
	private JwtTokenProvider jwtTokenProvider; 
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@PostMapping("check") 
	public Map<String, String> checkToken(@RequestBody String token)  
	{
		Map<String, String> map = new HashMap<>(); 
		
		if (jwtTokenProvider.validateTk(token)) 
		{
			String email = jwtTokenProvider.getEmail(token); 
			String sql = "SELECT * FROM users WHERE email = ?";   
			List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email); 
			if (sessionRows.size() == 0) 
				map.put("reponse", "email"); 
			else 
			    map.put("token", "ok");  
		}
		else 
			map.put("reponse", "invalide"); 
		return map; 
	}
}
