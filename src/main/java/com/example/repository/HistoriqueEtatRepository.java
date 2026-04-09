package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.HistoriqueEtat;
import com.example.model.Livraison;

public interface HistoriqueEtatRepository extends JpaRepository<HistoriqueEtat, Long>{ 
	
	
	Optional<List<HistoriqueEtat>> findByLivraison(Livraison livraison); 
	

}
