package com.example.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.repository.UserRepository;
import com.example.model.User; 


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private JdbcTemplate jdbcTemplate; 

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	String sql = "SELECT id FROM users WHERE email = ?";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email); 
        if (sessionRows.size() == 0) {
            throw new UsernameNotFoundException("Utilisateur non trouvée avec email: " + email);
        }
        Optional<User> userOp = userRepository.findById((Long)sessionRows.get(0).get("id")); 
		User user = userOp.get();
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), 
            user.getPassword(), 
            true,
            true,
            true,
            true,
            Collections.emptyList());
    }
}