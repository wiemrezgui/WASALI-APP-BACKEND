package com.example.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Gouvernorat; 

public interface GouvernoratRepository  extends JpaRepository<Gouvernorat, Long>{
	
	public Optional<Gouvernorat> findByGouv(String gouv); 

}
