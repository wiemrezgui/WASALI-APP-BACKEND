package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long>{ 
	
	public Optional<Privilege> findByPrivilege(String privilege);
}
