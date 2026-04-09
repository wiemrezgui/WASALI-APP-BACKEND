package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Livraison;
import com.example.model.User;
import com.example.model.User_Livraison;

public interface User_LivraisonRepository extends JpaRepository<User_Livraison, Long>{ 
	
	public Optional<List<User_Livraison>> findByLivraison(Livraison l);
	
	public Optional<List<User_Livraison>> findByUser(User user);
		

}
