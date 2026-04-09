package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Adresse;
import com.example.model.Gouvernorat;

public interface AdresseRepository extends JpaRepository<Adresse, Long> { 
	
	public Optional<Adresse> findByGouvAndRegionAndAutreAndCodePostal(Gouvernorat gouv, String region, String autre, Long codePostal);  
	
	public Optional<Adresse> findByAutre(String autre); 
	

}
