package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.model.Role;
import com.example.model.User;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByEmail(String email);  
	
	public Optional<List<User>> findByStatutAndActivationAndRole(String statut, boolean activation, Role role); 
	
	public Optional<List<User>> findByRole(Role role); 
	
	public Optional<List<User>> findByRoleAndActivation(Role role, boolean activation); 
	
	
}
