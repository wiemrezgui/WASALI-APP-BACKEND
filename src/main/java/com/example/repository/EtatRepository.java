package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Etat;

public interface EtatRepository extends JpaRepository<Etat, Long>{
	
	public Optional<Etat> findByEtat(String etat); 

}
