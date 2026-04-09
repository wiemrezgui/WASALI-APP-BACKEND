package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.TypeVehicule;

public interface TypeVehiculeRepository extends JpaRepository<TypeVehicule, Long>{ 
	
	public Optional<TypeVehicule> findByType(String type); 
	

}
