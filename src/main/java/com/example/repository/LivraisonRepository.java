package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Livraison;


public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
	
}
