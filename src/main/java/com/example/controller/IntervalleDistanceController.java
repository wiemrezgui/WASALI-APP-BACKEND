package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.IntervalleDistance;
import com.example.repository.IntervalleDistanceRepository;

@RestController
@RequestMapping("/api/intervalleDistance") 
public class IntervalleDistanceController {

	@Autowired 
	private IntervalleDistanceRepository intDistRepository; 
	
	@GetMapping("int") 
	public List<IntervalleDistance> getAllIntervallesDistance() {
		return intDistRepository.findAll(); 
	}
	
	
}
