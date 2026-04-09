package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.IntervalleDistance;
import com.example.model.User;
import com.example.model.User_IntervalleDistance;



public interface User_IntervalleDistanceRepository extends JpaRepository<User_IntervalleDistance, Long>{
	
	
	public Optional<List<User_IntervalleDistance>> findByIntervalleDistance(IntervalleDistance intervalle); 
	
	
	public Optional<User_IntervalleDistance> findByUserAndIntervalleDistance(User user, IntervalleDistance intervalle);  
	
	public Optional<List<User_IntervalleDistance>> findByUser(User user); 

}
