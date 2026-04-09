package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Etat;
import com.example.repository.EtatRepository;

@RestController
@RequestMapping("/api/etat")
public class EtatController {
	
	
	@Autowired
	private EtatRepository etatRepository; 
	
	@GetMapping("get")
	private List<Etat> getAllEtats()
	{
		return etatRepository.findAll(); 
	}
	

}
