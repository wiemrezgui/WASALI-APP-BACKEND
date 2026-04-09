package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.model.Role; 

@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	public Optional<Role> findRoleByRole(String role);
	public void deleteByRole(String role); 

}
