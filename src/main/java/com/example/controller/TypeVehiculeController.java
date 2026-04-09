package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.TypeVehicule;
import com.example.repository.TypeVehiculeRepository;

@RestController
@RequestMapping("/api/typeVehicule")
public class TypeVehiculeController { 
	
	@Autowired 
	private TypeVehiculeRepository typeVecRepository; 
	
	
	@GetMapping("type") 
	public List<TypeVehicule> getAllTypes() 
	{
		return typeVecRepository.findAll(); 
	}
	

}
