package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Abonnement;
import com.example.model.User;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
	
	public Optional<List<Abonnement>> findByUser(User user); 
	

}
