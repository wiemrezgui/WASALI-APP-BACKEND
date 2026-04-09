package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.model.Gouvernorat;
import com.example.model.Livraison;
import com.example.model.Privilege;
import com.example.model.User;
import com.example.model.User_Privilege;
import com.example.repository.GouvernoratRepository;
import com.example.repository.LivraisonRepository;
import com.example.repository.PrivilegeRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_PrivilegeRepository;

@RestController
@RequestMapping("/api/gouv")
public class GouvernoratController {
	
	@Autowired 
	private GouvernoratRepository gouvRepository; 
	
	@Autowired 
	private UserRepository userRepository; 
	
	@Autowired 
	private PrivilegeRepository privilegeRepository; 
	
	@Autowired
	private User_PrivilegeRepository userPrivRepository; 
	
	@Autowired 
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired 
	private LivraisonRepository livraisonRepository;

	@GetMapping("gv")
	public List<Gouvernorat> getAllGouvs() 
	{
		return gouvRepository.findAll();
	}
	
	@PostMapping("insertGv") 
	public Map<String, Object> insertGouv(@RequestPart("gv") String gv, @RequestPart("token") String token)  
	{
		Map<String, Object> map = new HashMap<>(); 
		String email = jwtTokenProvider.getEmail(token); 
		Optional<User> user = userRepository.findByEmail(email); 
		Optional<Privilege> priv = privilegeRepository.findByPrivilege("configuration");
		Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get());
		if (userPriv.isPresent()) 
		{
			Gouvernorat gouv = new Gouvernorat(); 
			gouv.setGouv(gv);
			gouvRepository.save(gouv);
		}
		else 
			map.put("reponse", "privilege"); 
		return map; 
	}
	
	@PostMapping("deleteGv") 
	public Map<String, Object> deleteGouv(@RequestPart("gv") String gv, @RequestPart("token") String token)  
	{
		Map<String, Object> map = new HashMap<>(); 
		String email = jwtTokenProvider.getEmail(token); 
		Optional<User> user = userRepository.findByEmail(email); 
		Optional<Privilege> priv = privilegeRepository.findByPrivilege("configuration");
		Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get());
		if (userPriv.isPresent()) 
		{
			Optional<Gouvernorat> gouv = gouvRepository.findByGouv(gv);
			List<Livraison> livs = livraisonRepository.findAll(); 
			boolean test = false; 
			for(Livraison liv : livs) 
				if (liv.getAdresseDest().getGouv().getGouv().equals(gouv.get().getGouv()) || liv.getAdressePickUp().getGouv().getGouv().equals(gouv.get().getGouv()))
					test = true; 
			if (!test)
			{
				gouvRepository.delete(gouv.get()); 
				map.put("reponse", true); 
			}
			else 
				map.put("reponse", "utilisé"); 
		}
		else 
			map.put("reponse", "privilege"); 
		return map; 
	}
	
	
	
	 
}
