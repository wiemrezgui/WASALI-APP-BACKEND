package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Role;
import com.example.repository.RoleRepository;

@RestController
@RequestMapping("/api/role")
public class RoleController { 
	
	@Autowired
	private RoleRepository roleRepository; 
	
	@GetMapping("rl") 
	public List<Role> getAllRoles() 
	{
		return roleRepository.findAll(); 
	}
	

}
