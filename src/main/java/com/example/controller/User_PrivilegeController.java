package com.example.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.model.Privilege;
import com.example.model.User;
import com.example.model.User_Privilege;
import com.example.repository.PrivilegeRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_PrivilegeRepository;



@RestController
@RequestMapping("/api/userPrivilege")
public class User_PrivilegeController {
	
	
 
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider; 
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private PrivilegeRepository privilegeRepository; 
	
	@Autowired
	private User_PrivilegeRepository userPrivRepository; 
	 
	
	
	@PostMapping("userPrivs") 
	public Map<String, Object> findUserPrivileges(@RequestBody String token)  
	{
		Map<String, Object> map = new HashMap<>();
		String email = jwtTokenProvider.getEmail(token);  
		String sql = "SELECT id FROM users WHERE email = ?";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email);
		User user = new User(); 
		user.setId((Long)sessionRows.get(0).get("id"));
		sql = "SELECT privilege FROM user_privilege up, privilege p WHERE user_id = ? and up.privilege_id = p.id";   
		sessionRows = jdbcTemplate.queryForList(sql, sessionRows.get(0).get("id"));  
		ArrayList<String> privileges = new ArrayList<>(); 
		for(Map<String, Object> row : sessionRows) 
			privileges.add(row.get("privilege").toString());  
		map.put("reponse", privileges); 
		return map; 	
	}

	@PostMapping("checkPrivilege")
	public Map<String, Object> checkPrivilegeLivr(@RequestBody Long id) {
		Map<String, Object> m = new HashMap<>();

		// Vérifier si l'utilisateur existe
		Optional<User> userOptional = userRepository.findById(id);
		if (!userOptional.isPresent()) {
			m.put("reponse", "Utilisateur non trouvé avec l'id: " + id);
			m.put("hasPrivilege", false);
			return m;
		}

		// Vérifier si le privilège "livraison" existe
		Optional<Privilege> privOptional = privilegeRepository.findByPrivilege("livraison");
		if (!privOptional.isPresent()) {
			m.put("reponse", "Privilège 'livraison' non trouvé dans la base");
			m.put("hasPrivilege", false);
			return m;
		}

		User user = userOptional.get();
		Privilege priv = privOptional.get();

		// Vérifier si l'utilisateur possède le privilège
		Optional<User_Privilege> userPrivOptional = userPrivRepository.findByUserAndPrivilege(user, priv);

		if (userPrivOptional.isPresent()) {
			m.put("reponse", true);
			m.put("hasPrivilege", true);
			m.put("message", "L'utilisateur possède le privilège 'livraison'");
		} else {
			m.put("reponse", "privilege");
			m.put("hasPrivilege", false);
			m.put("message", "L'utilisateur ne possède pas le privilège 'livraison'");
		}

		return m;
	}
}
