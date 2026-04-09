package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Privilege;
import com.example.model.User;
import com.example.model.User_Privilege;

public interface User_PrivilegeRepository extends JpaRepository<User_Privilege, Long>{
	
	
	public Optional<List<User_Privilege>> findByUser(User user); 
	public Optional<User_Privilege> findByUserAndPrivilege(User user, Privilege privilege); 
	public Optional<List<User_Privilege>> findByPrivilege(Privilege privilege); 

}
