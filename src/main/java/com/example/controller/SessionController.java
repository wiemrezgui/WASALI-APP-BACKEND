package com.example.controller;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/session")
public class SessionController {
	 
	 

	 @Autowired
	 private JdbcTemplate jdbcTemplate; 
	 
	 
	 
	 @PostMapping("create")
	 public Object createSession(HttpServletRequest request,  @RequestPart(name="photo", required = false) MultipartFile file, @RequestPart("email") String email, @RequestPart("password") String password, @RequestPart("nom") String nom, @RequestPart("prenom") String prenom, @RequestPart("role") String role, @RequestPart("tel") String tel, @RequestPart("genre") String genre) throws NumberFormatException, IOException {
		 Map<String, String> map = new HashMap<>(); 
		 int nb = 0; 
		 String sql = "SELECT  * FROM spring_session_attributes WHERE attribute_name='email' and UPPER(SUBSTRING(encode(attribute_bytes, 'hex'),15)) = ?";
		 byte[] bytes = email.getBytes(); 
		 StringBuilder hexChaine = new StringBuilder(); 
		 for (byte b : bytes) {
	            hexChaine.append(String.format("%02X", b));
	        }
		 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, hexChaine);
		 nb = sessionRows.size(); 
		 if (nb < 3) 
		 {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
			HttpSession session = request.getSession(true); 
			if (file != null) 
			{  
				session.setAttribute("email", email);
				session.setAttribute("photo", file.getBytes());
				session.setAttribute("password", passwordEncoder.encode(password));
				session.setAttribute("nom", nom);
				session.setAttribute("prenom", prenom);
				session.setAttribute("role", role);
				session.setAttribute("tel", tel);
				session.setAttribute("genre", genre);
			}
			else 
			{
				session.setAttribute("email", email);
				session.setAttribute("password", passwordEncoder.encode(password));
				session.setAttribute("nom", nom);
				session.setAttribute("prenom", prenom);
				session.setAttribute("role", role);
				session.setAttribute("tel", tel);
				session.setAttribute("genre", genre);
			}
			map.put("session_id", session.getId()); 
		 }
		 else
		 { 
			 map.put("reponse", "limite"); 

		 }
		 return map; 
	 }
	 
	 @PostMapping("check") 
	 public Object checkSession(@RequestBody String sessionId)
	 {
		 String sql = "SELECT * FROM spring_session, spring_session_attributes WHERE session_id = ? and primary_id = session_primary_id"; 
		 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, new Object[] {sessionId});
		 if (sessionRows.size() != 0) 
		 {
			 Map<Object, Object> m = new HashMap<>();  
			 for(Map<String, Object> row : sessionRows) 
				 m.put(row.get("attribute_name"), row.get("attribute_bytes"));  
			 Map<String, Object> map = new HashMap<>();
			 map.put("reponse", new String((byte[])m.get("prenom"), StandardCharsets.UTF_8).substring(7));
			 map.put("email", new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7));
			 map.put("role", new String((byte[])m.get("role"), StandardCharsets.UTF_8).substring(7));
			 return map; 
		 }
		 else 
			 return false; 
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
