package com.example.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.repository.AbonnementRepository;
import com.example.repository.UserRepository;
import com.example.model.Abonnement;
import com.example.model.User;


@RestController
@RequestMapping("/api/userIntervalle")
public class User_IntervalleDistanceController {
	
	

	
	@Autowired 
	private AbonnementRepository abonnementRepository;
	
	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	
	
	@PostMapping("livreur") 
	public Map<String, Object> findLivreurs(@RequestPart("id") String id , @RequestPart("vehicule") String vehicule, @RequestPart("idUser") String idUser) 
	{
		Map<String, Object> mp = new HashMap<>(); 
		String sql = "SELECT * FROM users u, user_intervalle_distance ui, type_vehicule t WHERE intervalle_distance_id = ? and u.id = ui.user_id and u.type_vehicule_id = t.id and t.id = ? order by tarif ASC";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, Long.parseLong(id), Long.parseLong(vehicule)); 	
		List<Map<String, Object>> usersInts = new LinkedList<>();
		for(Map<String, Object> row : sessionRows) 
		{
			 Optional<User> user = userRepository.findById((Long)row.get("user_id"));  
			 Optional<List<Abonnement>> abs = abonnementRepository.findByUser(user.get()); 
			 boolean test = false; 
			 if (abs.get().size() > 0) 
				 if (abs.get().get(abs.get().size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
					 test = true; 
			 if (test && (Long)row.get("user_id") != Long.parseLong(idUser)) 
			 {
				 Map<String, Object> map = new HashMap<>(); 
				 map.put("email", row.get("email")); 
				 map.put("nom", row.get("nom"));
				 map.put("prenom", row.get("prenom")); 
				 map.put("photo", row.get("photo"));
				 map.put("type", row.get("type")); 
				 map.put("tarif", row.get("tarif"));
				 map.put("genre", row.get("genre")); 
				 map.put("id", row.get("user_id")); 
				 usersInts.add(map); 
			 }
		 }	
		 mp.put("livreurs", usersInts);  
		 return mp; 
	}
	 
	

}
