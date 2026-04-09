package com.example.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Token;
import com.example.model.User;


public interface TokenRepository extends JpaRepository<Token, Long>{ 
	
	
	
	public Optional<Token> findByToken(String token);
	
	
	public void deleteById(long id); 

	
	public Optional<List<Token>> findByUser(User user); 
	
	
	
	
	
	
	
	
	
	
}
