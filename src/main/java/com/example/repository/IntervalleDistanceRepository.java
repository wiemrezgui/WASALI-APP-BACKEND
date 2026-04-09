package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.model.IntervalleDistance;


@RepositoryRestResource
public interface IntervalleDistanceRepository extends JpaRepository<IntervalleDistance, Long>{
	
	public Optional<IntervalleDistance> findByBorneGaucheAndBorneDroite(Long borneGauche, Long borneDroite); 

}
