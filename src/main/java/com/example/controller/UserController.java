package com.example.controller;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.model.Abonnement;
import com.example.model.IntervalleDistance;
import com.example.model.Livraison;
import com.example.model.Privilege;
import com.example.model.Role;
import com.example.model.TypeVehicule;
import com.example.model.User;
import com.example.model.User_IntervalleDistance;
import com.example.model.User_Livraison;
import com.example.model.User_Privilege;
import com.example.repository.AbonnementRepository;
import com.example.repository.IntervalleDistanceRepository;
import com.example.repository.LivraisonRepository;
import com.example.repository.PrivilegeRepository;
import com.example.repository.RoleRepository;
import com.example.repository.TypeVehiculeRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_IntervalleDistanceRepository;
import com.example.repository.User_LivraisonRepository;
import com.example.repository.User_PrivilegeRepository;
import com.example.service.EmailService;





@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PrivilegeRepository privilegeRepository;
	
	@Autowired
	private User_PrivilegeRepository userPrivRepository;
	
	@Autowired
	private TypeVehiculeRepository vehiculeRepository;

	
		
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider; 
	
	
	@Autowired
	private User_IntervalleDistanceRepository userDistRepository; 
	
	@Autowired 
	private IntervalleDistanceRepository intDistRepository; 
	
	@Autowired 
	private AbonnementRepository abonnementRepository; 
	
	@Autowired 
	private User_LivraisonRepository userLivRepository; 
	
	@Autowired 
	private LivraisonRepository livraisonRepository; 

	@Autowired 
	private EmailService emailService;
	
	@Autowired 
	private TypeVehiculeRepository typeRepository; 
	
	@Autowired 
	private User_IntervalleDistanceRepository userIntRepository;
	
	
	
	@PostMapping("email") 
	public boolean checkEmail(@RequestPart("email") String email) 
	{
		String sql = "SELECT * FROM users WHERE email = ?";   
		List<Map<String, Object>>sessionRows = jdbcTemplate.queryForList(sql, email); 
		return sessionRows.size() != 0;
	}
	
	
	

	
	
	@PutMapping("updatePass") 
	public Map<String, Object> updatePass(@RequestPart String email, @RequestPart String password)  
	{
		Map<String, Object> map = new HashMap<>(); 
		String sql = "SELECT id FROM users WHERE email = ?";   
		List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email); 
		Optional<User> userOp = userRepository.findById(((Long)sessionRows.get(0).get("id"))); 
		User user = userOp.get(); 
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(password)); 
		userRepository.save(user); 
		map.put("reponse", true); 
		return map;
	}
	

	@PostMapping("createClient") 
	 public Map<String, Object> createClient(@RequestPart("session_id") String sessionId,  @RequestPart("adresse") String adresse) throws IOException 
	 { 
		 Map<String, Object> map = new HashMap<>();  
		 String sql = "SELECT primary_id FROM spring_session WHERE session_id = ?"; 
		 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, sessionId); 
		 Object session_id = sessionRows.get(0).get("primary_id"); 
		 sql = "SELECT * FROM spring_session_attributes WHERE session_primary_id = ?";   
		 sessionRows = jdbcTemplate.queryForList(sql, session_id); 
		 Map<Object, Object> m = new HashMap<>();  
		 for(Map<String, Object> row : sessionRows) 
			 m.put(row.get("attribute_name"), row.get("attribute_bytes"));
		 Optional<Role> rl = roleRepository.findRoleByRole(new String((byte[])m.get("role"), StandardCharsets.UTF_8).substring(7)); 
		 String em = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7); 
		 sql = "SELECT * FROM users WHERE email = ?";   
		 sessionRows = jdbcTemplate.queryForList(sql, em);
		 if (sessionRows.size() != 0) 
			 map.put("reponse", "email"); 
		 else 
		 {
			 String email; 
			 if (m.containsKey("photo")) 
			 {
				 byte[] ph = (byte[])m.get("photo"); 
				 byte[] p = new byte[ph.length - 27]; 
				 System.arraycopy(ph, 27, p, 0, ph.length - 27); 
				 email = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7);
				 String password = new String((byte[])m.get("password"), StandardCharsets.UTF_8).substring(7);
				 String genre = new String((byte[])m.get("genre"), StandardCharsets.UTF_8).substring(7); 
				 String nom = new String((byte[])m.get("nom"), StandardCharsets.UTF_8).substring(7);
				 String prenom = new String((byte[])m.get("prenom"), StandardCharsets.UTF_8).substring(7); 
				 Long tel = Long.parseLong(new String((byte[])m.get("tel"), StandardCharsets.UTF_8).substring(7));
				
				 
				 User user = new User(email, password, nom, prenom, genre, adresse, tel, p, rl.get()); 
				 userRepository.save(user);
			 }
			 else 
			 {
				 email = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7);
				 String password = new String((byte[])m.get("password"), StandardCharsets.UTF_8).substring(7);
				 String genre = new String((byte[])m.get("genre"), StandardCharsets.UTF_8).substring(7); 
				 String nom = new String((byte[])m.get("nom"), StandardCharsets.UTF_8).substring(7);
				 String prenom = new String((byte[])m.get("prenom"), StandardCharsets.UTF_8).substring(7); 
				 Long tel = Long.parseLong(new String((byte[])m.get("tel"), StandardCharsets.UTF_8).substring(7));
				 User user = new User(email, password, nom, prenom, genre, adresse, tel, rl.get()); 
				 userRepository.save(user); 
			 } 
			 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("demande et suivi"); 
			 User_Privilege userPrivilege = new User_Privilege(); 
			 userPrivilege.setPrivilege(privilege.get());
			 sql = "SELECT id FROM users WHERE email = ?";   
			 sessionRows = jdbcTemplate.queryForList(sql, email); 
			 User user = new User(); 
			 user.setId((Long)sessionRows.get(0).get("id")); 
			 userPrivilege.setUser(user); 
			 userPrivRepository.save(userPrivilege); 
			 map.put("email", email); 
		 }
		 return map; 
	 }
	 
	
	@PostMapping("createLivreur")
	 public Map<String, Object> createLivreur(@RequestBody List<Map<String, Object>> map_rq) throws IOException 
	 { 
		 String sessionId = map_rq.get(0).get("session_id").toString().split("=")[1].substring(0, map_rq.get(0).get("session_id").toString().split("=")[1].length() - 1); 
		 Map<String, Object> map = new HashMap<>();  
		 String sql = "SELECT primary_id FROM spring_session WHERE session_id = ?"; 
		 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, sessionId); 
		 Object session_id = sessionRows.get(0).get("primary_id"); 
		 sql = "SELECT * FROM spring_session_attributes WHERE session_primary_id = ?";   
		 sessionRows = jdbcTemplate.queryForList(sql, session_id); 
		 Map<Object, Object> m = new HashMap<>();  
		 for(Map<String, Object> row : sessionRows) 
			 m.put(row.get("attribute_name"), row.get("attribute_bytes"));
		 Optional<Role> rl = roleRepository.findRoleByRole(new String((byte[])m.get("role"), StandardCharsets.UTF_8).substring(7)); 
		 Optional<TypeVehicule> type = vehiculeRepository.findByType(map_rq.get(2).get("vehicule").toString().split("=")[1].substring(0, map_rq.get(2).get("vehicule").toString().split("=")[1].length() - 1)); 
		 String em = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7); 
		 sql = "SELECT * FROM users WHERE email = ?";   
		 sessionRows = jdbcTemplate.queryForList(sql, em);
		 if (sessionRows.size() != 0) 
			 map.put("reponse", "email"); 
		 else 
		 {
			 String email = ""; 
			 Long id; 
			 if (m.containsKey("photo")) 
			 {
				 byte[] ph = (byte[])m.get("photo"); 
				 byte[] p = new byte[ph.length - 27];      
				 System.arraycopy(ph, 27, p, 0, ph.length - 27); 
				 email = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7);
				 String password = new String((byte[])m.get("password"), StandardCharsets.UTF_8).substring(7);
				 String genre = new String((byte[])m.get("genre"), StandardCharsets.UTF_8).substring(7); 
				 String nom = new String((byte[])m.get("nom"), StandardCharsets.UTF_8).substring(7);
				 String prenom = new String((byte[])m.get("prenom"), StandardCharsets.UTF_8).substring(7); 
				 Long tel = Long.parseLong(new String((byte[])m.get("tel"), StandardCharsets.UTF_8).substring(7));
				 User user = new User(email, password, nom, prenom, genre, null, tel, p, rl.get(), type.get()); 
				 userRepository.save(user); 
				 id = user.getId(); 
				 for (Map.Entry<String, Object> entre : map_rq.get(1).entrySet())  
				 {
					 Long borneGauche = Long.parseLong(entre.getKey().split(",")[0]);  
					 Long borneDroite = Long.parseLong(entre.getKey().split(",")[1]); 
					 Optional<IntervalleDistance> dist = intDistRepository.findByBorneGaucheAndBorneDroite(borneGauche, borneDroite); 
					 Optional<User> crUser = userRepository.findByEmail(email);  
					 double tarif;
					 @SuppressWarnings("unchecked")
					Object value =  ((LinkedHashMap<String, Object>)entre.getValue()).get("value"); 
					 if (value instanceof Integer) 
						 tarif = ((Integer)value).doubleValue(); 
					 else 
						 tarif = ((Double)value).doubleValue();
					 User_IntervalleDistance userDist = new User_IntervalleDistance(crUser.get(), dist.get(), tarif); 
					 userDistRepository.save(userDist); 
				 }
			 }
			 else 
			 {
				 email = new String((byte[])m.get("email"), StandardCharsets.UTF_8).substring(7);
				 String password = new String((byte[])m.get("password"), StandardCharsets.UTF_8).substring(7);
				 String genre = new String((byte[])m.get("genre"), StandardCharsets.UTF_8).substring(7); 
				 String nom = new String((byte[])m.get("nom"), StandardCharsets.UTF_8).substring(7);
				 String prenom = new String((byte[])m.get("prenom"), StandardCharsets.UTF_8).substring(7); 
				 Long tel = Long.parseLong(new String((byte[])m.get("tel"), StandardCharsets.UTF_8).substring(7)); 
				 User user = new User(email, password, nom, prenom, genre, null, tel, rl.get(), type.get()); 
				 userRepository.save(user);
				 id = user.getId();
				 for (Map.Entry<String, Object> entre : map_rq.get(1).entrySet())  
				 {
					 Long borneGauche = Long.parseLong(entre.getKey().split(",")[0]);  
					 Long borneDroite = Long.parseLong(entre.getKey().split(",")[1]);
					 Optional<IntervalleDistance> dist = intDistRepository.findByBorneGaucheAndBorneDroite(borneGauche, borneDroite); 
					 Optional<User> crUser = userRepository.findByEmail(email);
					 double tarif;
					 @SuppressWarnings("unchecked")
					Object value =  ((LinkedHashMap<String, Object>)entre.getValue()).get("value");
					 if (value instanceof Integer) 
						 tarif = ((Integer)value).doubleValue(); 
					 else 
						 tarif = ((Double)value).doubleValue();
					 User_IntervalleDistance userDist = new User_IntervalleDistance(crUser.get(), dist.get(), tarif); 
					 userDistRepository.save(userDist); 
				 }
			 }
			 map.put("reponse", id); 
		 }
		 return map;
	 }
	
	@PostMapping("cin") 
	public void setCIN(@RequestPart("cin") MultipartFile cin, @RequestPart("id") String id) throws IOException 
	{
		Optional<User> user = userRepository.findById(Long.parseLong(id)); 
		User liv = user.get(); 
		liv.setCin(cin.getBytes());
		userRepository.save(liv); 
	}
	
	 @PostMapping("adresse") 
	 public Map<String, Object> findAdresse(@RequestBody String token) 
	 {
		 	Map<String, Object> map = new HashMap<>();
		 	String email = jwtTokenProvider.getEmail(token);  
		 	Optional<User> client = userRepository.findByEmail(email); 
		 	Optional<Privilege> priv = privilegeRepository.findByPrivilege("demande et suivi"); 
			Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(client.get(), priv.get());
			if (userPriv.isPresent()) 
			{
				String sql = "SELECT adresse FROM users WHERE email = ?";   
				List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email); 
				map.put("adresse", sessionRows.get(0).get("adresse"));
			} 
			else 
				map.put("reponse", "privilege"); 
			return map;
	 }
	 
	 
	 @PostMapping("role") 
	 public Map<String, Object> findRole(@RequestBody String token) 
	 {
		 Map<String, Object> map = new HashMap<>();
		try 
		{
				String email = jwtTokenProvider.getEmail(token);  
				String sql = "SELECT role FROM users u, roles r WHERE email = ? and u.role_id = r.id";   
				List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, email); 
				if (sessionRows.size() == 0) 
					map.put("reponse", "email"); 
				else 
					map.put("role", sessionRows.get(0).get("role")); 
		}
		catch (Exception e) 
		{
				map.put("reponse", "invalide ou expire");
		} 
		return map;
	 }
	 
	 
	 @PostMapping("id") 
	 public Long iduser(@RequestBody String token)
	 {
			String email = jwtTokenProvider.getEmail(token); 
			Optional<User> user = userRepository.findByEmail(email); 
			return user.get().getId(); 
	 }
	 
	 
	@PostMapping("photo") 
	 public Map<String, Object> getPhoto(@RequestBody String token) 
	 {
		 Map<String, Object> m = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 if (!user.get().getRole().getRole().equals("Admin")) 
			 if (user.get().getPhoto() != null) 
				 m.put("photo", user.get().getPhoto()); 
			 else 
				 m.put("genre", user.get().getGenre()); 
		 else 
			 m.put("admin", true); 
		 return m; 
	 }
	
	@PostMapping("demandesIns") 
	public List<Map<String, Object>> getDemandes(@RequestBody String token) 
	{
		Optional<Role> role = roleRepository.findRoleByRole("Livreur"); 
		Optional<List<User>> dms = userRepository.findByRoleAndActivation(role.get(), false); 
		List<User> dmnds = dms.get().stream()
			    .sorted(Comparator.comparingLong(User::getId).reversed())
			    .collect(Collectors.toList());
		List<Map<String, Object>> livreursInfos = new LinkedList<>();
		for(User livreur : dmnds)
		{
			Map<String, Object> m = new HashMap<>();
			m.put("livreur", livreur); 
			Optional<List<User_IntervalleDistance>> tarifs = userDistRepository.findByUser(livreur); 
			m.put("intDists", tarifs.get()); 
			livreursInfos.add(m); 
		}
		return livreursInfos;
	}
	 
	
	 
	 
	 @PostMapping("posLivreur")
	 public void posLivreur(@RequestPart("id") String id, @RequestPart("pos") String pos) 
	 {
		 Optional<User> user = userRepository.findById(Long.parseLong(id));
		 User userr = user.get(); 
		 userr.setLocalisation(pos);  
		 userRepository.save(userr);
	 }
	 
	 @PostMapping("tkPosLivreur")
	 public void tkPosLivreur(@RequestBody String token) 
	 {
		String email = jwtTokenProvider.getEmail(token); 
		Optional<User> user = userRepository.findByEmail(email);
		User userr = user.get(); 
		userr.setLocalisation("");  
		userRepository.save(userr);
	 }
	 
	 @PostMapping("locLivreur")
	 public Map<String, String> locLivreur(@RequestBody Long id){
		 Map<String, String> m = new HashMap<>();
		 Optional<User> userr = userRepository.findById(id);  
		 m.put("reponse", userr.get().getLocalisation());
		 return m; 
	 }
	 
	 
	 
	 @PostMapping("clients") 
	 public Map<String, Object> findClients(@RequestBody String token) 
	 {
		 Map<String, Object> m = new HashMap<>();   
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("demande et suivi");
		 Optional<List<User_Privilege>> cltss = userPrivRepository.findByPrivilege(privilege.get());
		 List<User> clts = new LinkedList<>(); 
		 for(User_Privilege client : cltss.get())
			 clts.add(client.getUser());  
	     m.put("clients", clts); 
		 return m; 
	 }
	 
	 @PostMapping("validateDm") 
	 public Map<String, Object> validateDm(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 User liv = livreur.get();
			 User lv = new User(); 
			 lv.setEmail(liv.getEmail()); 
			 lv.setGenre(liv.getGenre()); 
			 lv.setPhoto(liv.getPhoto()); 
			 lv.setNom(liv.getNom()); 
			 lv.setPassword(liv.getPassword()); 
			 lv.setPrenom(liv.getPrenom()); 
			 lv.setRole(liv.getRole()); 
			 lv.setTel(liv.getTel()); 
			 lv.setTypeVehicule(liv.getTypeVehicule());  
			 lv.setCin(liv.getCin());
			 lv.setActivation(true);
			 lv.setStatut("libre");
			 userRepository.save(lv);
			 Optional<List<User_IntervalleDistance>> tarifs = userDistRepository.findByUser(liv);
			 for(User_IntervalleDistance ud : tarifs.get()) 
			 {
				 User_IntervalleDistance userInt =  new User_IntervalleDistance(); 
				 userInt.setUser(lv); 
				 userInt.setIntervalleDistance(ud.getIntervalleDistance()); 
				 userInt.setTarif(ud.getTarif());
				 userDistRepository.save(userInt); 
			 }
			 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("livraison"); 
			 User_Privilege userPrivv = new User_Privilege(); 
			 userPrivv.setPrivilege(privilege.get()); 
			 userPrivv.setUser(lv); 
			 userPrivRepository.save(userPrivv); 
			 userRepository.delete(liv);
			 Abonnement ab = new Abonnement(); 
			 ab.setDateDeb(LocalDateTime.now()); 
			 ab.setDateFin(LocalDateTime.now().plusMonths(2)); 
			 ab.setUser(lv); 
			 abonnementRepository.save(ab);
			 boolean ok = emailService.send(lv.getEmail(), "http://localhost:4200/connexion", "Votre compte est cree avec succes. Vous pouvez vous connecter via ce"); 
			 if (ok)
				 map.put("reponse", true);
			 else 
				 map.put("reponse", "transmission"); 
		 }
		 else
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("rejectDm") 
	 public Map<String, Object> rejectDm(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 User liv = livreur.get(); 
			 userRepository.delete(liv); 
			 boolean ok = emailService.send(liv.getEmail(), "http://localhost:4200/inscription", "Desole. Vos tarifs ne sont pas acceptes ou votre CIN est invalide. Vous pouvez refaire votre inscription via ce"); 
			 if (ok)
				 map.put("reponse", true);
			 else 
				 map.put("reponse", "transmission");
		 }
		 else
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("fdLivreurs") 
	 public Map<String, Object> findLivreurs(@RequestBody String token) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("livraison"); 
		 Optional<List<User_Privilege>> livreurs = userPrivRepository.findByPrivilege(privilege.get());
		 List<User_Privilege>livs = livreurs.get().stream()
				    .sorted(Comparator.comparingLong(User_Privilege::getId).reversed())
				    .collect(Collectors.toList());
		 List<Map<String, Object>> livrs = new LinkedList<>(); 
		 for(User_Privilege livreur : livs)
		 {
			 if (livreur.getUser().isActivation()) 
			 {
				 Map<String, Object> mp = new HashMap<>(); 
				 mp.put("livreur", livreur.getUser());
				 int nb = 0; 
				 Optional<List<User_Livraison>> userLivs = userLivRepository.findByUser(livreur.getUser()); 
				 for(User_Livraison userLiv : userLivs.get()) 
				 {
					 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(userLiv.getLivraison()); 
					 if (usersLiv.get().get(0).getId() == userLiv.getId()) 
					 {
						 if (usersLiv.get().get(0).getId() > usersLiv.get().get(1).getId())
						 {
							 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
							 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, userLiv.getLivraison().getId()); 
							 if (sessionRows.get(0).get("etat").equals("effectuée")) 
								 nb++; 
						 }
					 }
					 else 
					 {
						 if (usersLiv.get().get(1).getId() > usersLiv.get().get(0).getId())
						 {
							 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
							 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, userLiv.getLivraison().getId()); 
							 if (sessionRows.get(0).get("etat").equals("effectuée")) 
								 nb++; 
						 }
					 }
					 
				 }
				 mp.put("nb", nb); 
				 Optional<List<User_IntervalleDistance>> tarifs = userDistRepository.findByUser(livreur.getUser()); 
				 mp.put("tarifs", tarifs.get()); 
				 Optional<List<Abonnement>> ab = abonnementRepository.findByUser(livreur.getUser()); 
				 if (ab.get().size() > 0) 
				 {
					 List<Abonnement> abs = ab.get();
					 if (abs.size() >= 2 || abs.size() == 1 && abs.get(0).getDateFin().isBefore(LocalDateTime.now())) 
						 mp.put("abs", true);
					 else 
						 mp.put("abs", false);
					 if (abs.get(abs.size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
						 mp.put("ab", false);  
					 else 
						 mp.put("ab", true);
				 }
				 else 
				 {
					 mp.put("ab", true); 
					 mp.put("abs", false);
				 }
				 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(livreur.getUser());
				 if (userPrivs.get().size() == 6) 
					 mp.put("pr", false); 
				 else 
					 mp.put("pr", true);
				 if (userPrivs.get().size() == 1) 
					 mp.put("prDt", false); 
				 else 
					 mp.put("prDt", true);
				 if (livreur.getUser().getStatut().equals("non libre")) 
					 mp.put("lb", false); 
				 else 
				 {
					 Optional<List<User_Livraison>> list = userLivRepository.findByUser(livreur.getUser());
					 boolean test = false; 
					 for(User_Livraison liv : list.get()) 
					 {
						 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
						 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
						 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
						 {
							 test = true; 
							 break;  
						 }
					 }
					 if (test) 
						 mp.put("lb", false); 
					 else 
						 mp.put("lb", true); 
				 }
				 livrs.add(mp); 
			 }
		 } 
		 map.put("livreurs", livrs); 
		 return map; 
	 }
	 
	 @PostMapping("deleteLiv") 
	 public Map<String, Object> deleteLiv(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 if (livreur.get().getStatut().equals("non libre")) 
					 map.put("reponse", "livreur nb"); 
				 else 
				 {
					 Optional<List<User_Livraison>> list = userLivRepository.findByUser(livreur.get());
					 boolean test = false; 
					 for(User_Livraison liv : list.get()) 
					 {
						 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
						 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
						 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
						 {
							 test = true; 
							 break;  
						 }
					 }
					 if (test) 
						 map.put("reponse", "client nb"); 
					 else 
					 {
						 Optional<List<Abonnement>> ab = abonnementRepository.findByUser(livreur.get()); 
						 if (ab.get().size() > 0)  
						 {
							 List<Abonnement> abs = ab.get();
							 if (abs.get(abs.size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
								 map.put("reponse", "ab");  
							 else 
							 {
								 List<Livraison> livs = new LinkedList<>(); 
								 for(User_Livraison userLiv : list.get()) 
									 livs.add(userLiv.getLivraison()); 
								 livraisonRepository.deleteAll(livs);
								 userRepository.delete(livreur.get()); 
								 map.put("reponse", true);
							 }
						 }
						 else 
						 {
							 List<Livraison> livs = new LinkedList<>(); 
							 for(User_Livraison userLiv : list.get()) 
								 livs.add(userLiv.getLivraison()); 
							 livraisonRepository.deleteAll(livs);
							 userRepository.delete(livreur.get()); 
							 map.put("reponse", true);
						 } 
					 }
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 } 
	 
	 @PostMapping("deleteCl") 
	 public Map<String, Object> deleteCl(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 if (client.isPresent()) 
			 {
				 if (client.get().getStatut()!= null && client.get().getStatut().equals("non libre")) 
					 map.put("reponse", "livreur nb"); 
				 else 
				 {
					 Optional<List<User_Livraison>> list = userLivRepository.findByUser(client.get());
					 boolean test = false; 
					 for(User_Livraison liv : list.get()) 
					 {
						 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
						 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
						 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
						 {
							 test = true; 
							 break;  
						 }
					 }
					 if (test) 
						 map.put("reponse", "client nb"); 
					 else 
					 {
						 Optional<List<Abonnement>> ab = abonnementRepository.findByUser(client.get()); 
						 if (ab.get().size() > 0) 
						 {
							 List<Abonnement> abs = ab.get();
							 if (abs.get(abs.size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
								 map.put("reponse", "ab");  
							 else 
							 {
								 Optional<List<User_Livraison>> userLivs = userLivRepository.findByUser(client.get()); 
								 List<Livraison> livs = new LinkedList<>(); 
								 for(User_Livraison userLiv : userLivs.get()) 
									 livs.add(userLiv.getLivraison()); 
								 livraisonRepository.deleteAll(livs);
								 userRepository.delete(client.get()); 
								 map.put("reponse", true);
							 }
						 }
						 else 
						 {
							 Optional<List<User_Livraison>> userLivs = userLivRepository.findByUser(client.get()); 
							 List<Livraison> livs = new LinkedList<>(); 
							 for(User_Livraison userLiv : userLivs.get()) 
								 livs.add(userLiv.getLivraison()); 
							 livraisonRepository.deleteAll(livs);
							 userRepository.delete(client.get()); 
							 map.put("reponse", true);
						 }  
					 }
				 }
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 } 
	 
	 @PostMapping("addAbnm") 
	 public Map<String, Object> addAbnm(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Abonnement ab = new Abonnement(); 
				 ab.setDateDeb(LocalDateTime.now()); 
				 ab.setDateFin(LocalDateTime.now().plusMonths(1)); 
				 ab.setUser(livreur.get()); 
				 abonnementRepository.save(ab);
				 map.put("reponse", true);
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("getAbnms") 
	 public Map<String, Object> getAbnms(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<List<Abonnement>> abs = abonnementRepository.findByUser(livreur.get());
				 
				 if (abs.get().size() == 0) 
					 map.put("reponse", false); 
				 else 
				 {
					 List<Abonnement> abns = abs.get(); 
					 if (abns.size() >= 2 || abns.size() == 1 && abns.get(0).getDateFin().isBefore(LocalDateTime.now())) 
					 {
						 List<Map<String, Object>> abss = new LinkedList<>(); 
						 DateTimeFormatter ftt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
						 for(Abonnement ab : abns) 
						 {
							 if (ab.getDateFin().isBefore(LocalDateTime.now())) 
							 {
								 Map<String, Object> mp = new HashMap<>(); 
								 mp.put("id", ab.getId());
								 mp.put("dateDeb", ab.getDateDeb().format(ftt)); 
								 mp.put("dateFin", ab.getDateFin().format(ftt)); 
								 abss.add(mp);
							 } 
						 }
						 map.put("abns", abss); 
						 map.put("reponse", true); 
					 }
					 else
						 map.put("reponse", false); 
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("getPrs") 
	 public Map<String, Object> getPrs(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(livreur.get());
				 if (userPrivs.get().size() == 1) 
					 map.put("reponse", "aucun");
				 else 
				 {
					 map.put("reponse", true); 
					 if (livreur.get().getRole().getRole().equals("Livreur")) 
					 {
						 List<User_Privilege> privs = new LinkedList<>(); 
						 for(User_Privilege prv : userPrivs.get())
							 if (!prv.getPrivilege().getPrivilege().equals("livraison")) 
								 privs.add(prv); 
						 map.put("privs", privs); 
					 }
					 else
					 {
						 List<User_Privilege> privs = new LinkedList<>(); 
						 for(User_Privilege prv : userPrivs.get())
							 if (!prv.getPrivilege().getPrivilege().equals("demande et suivi")) 
								 privs.add(prv); 
						 map.put("privs", privs);
					 }
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("getPrsCl") 
	 public Map<String, Object> getPrsCl(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 if (client.isPresent()) 
			 {
				 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(client.get());
				 if (userPrivs.get().size() == 1) 
					 map.put("reponse", "aucun");
				 else 
				 {
					 map.put("reponse", true); 
					 if (client.get().getRole().getRole().equals("Client")) 
					 {
						 List<User_Privilege> privs = new LinkedList<>(); 
						 for(User_Privilege prv : userPrivs.get())
							 if (!prv.getPrivilege().getPrivilege().equals("demande et suivi")) 
								 privs.add(prv); 
						 map.put("privs", privs); 
					 }
					 else 
					 {
						 List<User_Privilege> privs = new LinkedList<>(); 
						 for(User_Privilege prv : userPrivs.get())
							 if (!prv.getPrivilege().getPrivilege().equals("livraison")) 
								 privs.add(prv); 
						 map.put("privs", privs); 
					 }
				 }
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("getPrsAdd") 
	 public Map<String, Object> getPrsAdd(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(livreur.get());
				 if (userPrivs.get().size() == 6) 
					 map.put("reponse", "full");
				 else 
				 {
					 List<Privilege> privs = privilegeRepository.findAll(); 
					 List<Privilege> privss = new LinkedList<>(); 
					 for(Privilege pr : privs) 
					 {
						 boolean test = false; 
						 for(User_Privilege prv : userPrivs.get()) 
							 if (pr.getPrivilege().equals(prv.getPrivilege().getPrivilege()))
							 {
								 test = true; 
								 break; 
							 }
						 if (!test) 
							 privss.add(pr); 
					 }
					 map.put("reponse", true); 
					 map.put("privs", privss);
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 
	 @PostMapping("getPrsClAdd") 
	 public Map<String, Object> getPrsClAdd(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 if (client.isPresent()) 
			 {
				 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(client.get());
				 if (userPrivs.get().size() == 6) 
					 map.put("reponse", "full");
				 else 
				 {
					 List<Privilege> privs = privilegeRepository.findAll(); 
					 List<Privilege> privss = new LinkedList<>(); 
					 for(Privilege pr : privs) 
					 {
						 boolean test = false; 
						 for(User_Privilege prv : userPrivs.get()) 
							 if (pr.getPrivilege().equals(prv.getPrivilege().getPrivilege()))
							 {
								 test = true; 
								 break; 
							 }
						 if (!test) 
							 privss.add(pr); 
					 }
					 map.put("reponse", true); 
					 map.put("privs", privss);
				 }
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("deleteAbnm") 
	 public Map<String, Object> deleteAbnm(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("idd") String idd) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<Abonnement> ab = abonnementRepository.findById(Long.parseLong(idd)); 
				 if (ab.isPresent()) 
				 {
					 abonnementRepository.delete(ab.get()); 
					 map.put("reponse", true);
				 }
				 else 
					 map.put("reponse", "abn"); 
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("deletePriv") 
	 public Map<String, Object> deletePriv(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("idd") String idd) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<Privilege> priv = privilegeRepository.findById(Long.parseLong(idd)); 
				 Optional<User_Privilege> userPrivv = userPrivRepository.findByUserAndPrivilege(livreur.get(), priv.get()); 
				 if (!userPrivv.isPresent()) 
					 map.put("reponse", "deja"); 
				 else 
				 {
					 if (priv.get().getPrivilege().equals("demande et suivi")) 
					 {
						 Optional<List<User_Livraison>> livs = userLivRepository.findByUser(livreur.get()); 
						 boolean test = true; 
						 for(User_Livraison liv : livs.get()) 
						 {
							 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
							 if (usersLiv.get().get(0).getId() == liv.getId()) 
							 {
								 if (usersLiv.get().get(0).getId() < usersLiv.get().get(1).getId())
								 {
									 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
									 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
									 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
									 {
										 test = false; 
										 break; 
									 }
								 }
							 }
							 else 
							 {
								 if (usersLiv.get().get(1).getId() < usersLiv.get().get(0).getId())
								 {
									 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
									 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
									 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
									 {
										 test = false; 
										 break; 
									 }
								 }
							 }
						 }
						 if (!test) 
							 map.put("reponse", "client demande"); 
						 else 
						 { 
							 for(User_Livraison liv : livs.get()) 
							 {
								 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
								 if (usersLiv.get().get(0).getId() == liv.getId()) 
								 {
									 if (usersLiv.get().get(0).getId() < usersLiv.get().get(1).getId())
										 livraisonRepository.delete(liv.getLivraison());
								 }
								 else 
								 {
									 if (usersLiv.get().get(1).getId() < usersLiv.get().get(0).getId())
										 livraisonRepository.delete(liv.getLivraison());
								 }
							 }
							 map.put("reponse", true); 
							 userPrivRepository.delete(userPrivv.get());
						 }
					 }
					 else if (priv.get().getPrivilege().equals("livraison")) 
					 {
								 Optional<List<User_Livraison>> livs = userLivRepository.findByUser(livreur.get()); 
								 boolean test = true; 
								 for(User_Livraison liv : livs.get()) 
								 {
									 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
									 if (usersLiv.get().get(0).getId() == liv.getId()) 
									 {
										 if (usersLiv.get().get(0).getId() > usersLiv.get().get(1).getId())
										 {
											 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
											 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
											 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
											 {
												 test = false; 
												 break; 
											 }
										 }
									 }
									 else 
									 {
										 if (usersLiv.get().get(1).getId() > usersLiv.get().get(0).getId())
										 {
											 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
											 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
											 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
											 {
												 test = false; 
												 break; 
											 }
										 }
									 }
								 }
								 if (!test) 
									 map.put("reponse", "livraison"); 
								 else 
								 { 
									 Optional<List<Abonnement>> abs = abonnementRepository.findByUser(livreur.get()); 
									 if (abs.get().size() >= 1 && abs.get().get(abs.get().size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
									 {
										 map.put("reponse", "abonnement"); 
									 }
									 else 
									 {
										 for(User_Livraison liv : livs.get()) 
										 {
											 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
											 if (usersLiv.get().get(0).getId() == liv.getId()) 
											 {
												 if (usersLiv.get().get(0).getId() > usersLiv.get().get(1).getId())
													 livraisonRepository.delete(liv.getLivraison());
											 }
											 else 
											 {
												 if (usersLiv.get().get(1).getId() > usersLiv.get().get(0).getId())
													 livraisonRepository.delete(liv.getLivraison());
											 }
										 }
										 Optional<List<User_IntervalleDistance>> userInts = userIntRepository.findByUser(livreur.get()); 
										 userIntRepository.deleteAll(userInts.get());
										 map.put("reponse", true); 
										 userPrivRepository.delete(userPrivv.get());
									 }
								 }
					 }
					 else 
					 {
						 map.put("reponse", true); 
						 userPrivRepository.delete(userPrivv.get());
					 }
					 
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("deletePrivCl") 
	 public Map<String, Object> deletePrivCl(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("idd") String idd) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 if (client.isPresent()) 
			 {
				 Optional<Privilege> priv = privilegeRepository.findById(Long.parseLong(idd)); 
				 Optional<User_Privilege> userPrivv = userPrivRepository.findByUserAndPrivilege(client.get(), priv.get()); 
				 if (!userPrivv.isPresent()) 
					 map.put("reponse", "deja"); 
				 else 
				 {
					 if (priv.get().getPrivilege().equals("demande et suivi")) 
					 {
						 Optional<List<User_Livraison>> livs = userLivRepository.findByUser(client.get()); 
						 boolean test = true; 
						 for(User_Livraison liv : livs.get()) 
						 {
							 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
							 if (usersLiv.get().get(0).getId() == liv.getId()) 
							 {
								 if (usersLiv.get().get(0).getId() < usersLiv.get().get(1).getId())
								 {
									 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
									 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
									 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
									 {
										 test = false; 
										 break; 
									 }
								 }
							 }
							 else 
							 {
								 if (usersLiv.get().get(1).getId() < usersLiv.get().get(0).getId())
								 {
									 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
									 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
									 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
									 {
										 test = false; 
										 break; 
									 }
								 }
							 }
						 }
						 if (!test) 
							 map.put("reponse", "client demande"); 
						 else 
						 { 
							 for(User_Livraison liv : livs.get()) 
							 {
								 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
								 if (usersLiv.get().get(0).getId() == liv.getId()) 
								 {
									 if (usersLiv.get().get(0).getId() < usersLiv.get().get(1).getId())
										 livraisonRepository.delete(liv.getLivraison());
								 }
								 else 
								 {
									 if (usersLiv.get().get(1).getId() < usersLiv.get().get(0).getId())
										 livraisonRepository.delete(liv.getLivraison());
								 }
							 }
							 map.put("reponse", true); 
							 userPrivRepository.delete(userPrivv.get());
						 }
					 }
					 else if (priv.get().getPrivilege().equals("livraison")) 
					 {
								 Optional<List<User_Livraison>> livs = userLivRepository.findByUser(client.get()); 
								 boolean test = true; 
								 for(User_Livraison liv : livs.get()) 
								 {
									 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
									 if (usersLiv.get().get(0).getId() == liv.getId()) 
									 {
										 if (usersLiv.get().get(0).getId() > usersLiv.get().get(1).getId())
										 {
											 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
											 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
											 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
											 {
												 test = false; 
												 break; 
											 }
										 }
									 }
									 else 
									 {
										 if (usersLiv.get().get(1).getId() > usersLiv.get().get(0).getId())
										 {
											 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1";
											 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
											 if (sessionRows.get(0).get("etat").equals("acceptée") || sessionRows.get(0).get("etat").equals("colis récupéré")) 
											 {
												 test = false; 
												 break; 
											 }
										 }
									 }
								 }
								 if (!test) 
									 map.put("reponse", "livraison"); 
								 else 
								 { 
									 Optional<List<Abonnement>> abs = abonnementRepository.findByUser(client.get()); 
									 if (abs.get().size() >= 1 && abs.get().get(abs.get().size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
									 {
										 map.put("reponse", "abonnement"); 
									 }
									 else 
									 {
										 for(User_Livraison liv : livs.get()) 
										 {
											 Optional<List<User_Livraison>> usersLiv = userLivRepository.findByLivraison(liv.getLivraison()); 
											 if (usersLiv.get().get(0).getId() == liv.getId()) 
											 {
												 if (usersLiv.get().get(0).getId() > usersLiv.get().get(1).getId())
													 livraisonRepository.delete(liv.getLivraison());
											 }
											 else 
											 {
												 if (usersLiv.get().get(1).getId() > usersLiv.get().get(0).getId())
													 livraisonRepository.delete(liv.getLivraison());
											 }
										 }
										 Optional<List<User_IntervalleDistance>> userInts = userIntRepository.findByUser(client.get()); 
										 userIntRepository.deleteAll(userInts.get());
										 map.put("reponse", true); 
										 userPrivRepository.delete(userPrivv.get());
									 }
								 }
					 }
					 else 
					 {
						 map.put("reponse", true); 
						 userPrivRepository.delete(userPrivv.get());
					 }
				 }
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("ajouterPriv") 
	 public Map<String, Object> ajouterPriv(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("priv") String privv) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 Optional<Privilege> priv = privilegeRepository.findByPrivilege(privv); 
				 Optional<User_Privilege> userPrivv = userPrivRepository.findByUserAndPrivilege(livreur.get(), priv.get()); 
				 if (userPrivv.isPresent()) 
					 map.put("reponse", "deja"); 
				 else 
				 {
					 map.put("reponse", true); 
					 User_Privilege userPrivvv = new User_Privilege(); 
					 userPrivvv.setUser(livreur.get());
					 userPrivvv.setPrivilege(priv.get()); 
					 userPrivRepository.save(userPrivvv); 
				 }
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("ajouterPrivCl") 
	 public Map<String, Object> ajouterPrivCl(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("priv") String privv) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 if (client.isPresent()) 
			 {
				 Optional<Privilege> priv = privilegeRepository.findByPrivilege(privv); 
				 Optional<User_Privilege> userPrivv = userPrivRepository.findByUserAndPrivilege(client.get(), priv.get()); 
				 if (userPrivv.isPresent()) 
					 map.put("reponse", "deja"); 
				 else 
				 {
					 map.put("reponse", true); 
					 User_Privilege userPrivvv = new User_Privilege(); 
					 userPrivvv.setUser(client.get());
					 userPrivvv.setPrivilege(priv.get()); 
					 userPrivRepository.save(userPrivvv); 
				 }
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 } 
	 
	 @PostMapping("intsPrv") 
	 public void setTarifs(@RequestPart("id") String id, @RequestPart("tarif0") String tarif, @RequestPart("tarif1") String tariff, @RequestPart("tarif2") String tarifff, @RequestPart("tarif3") String tariffff, @RequestPart("type") String type) 
	 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id)); 
			 User cl = client.get(); 
			 cl.setActivation(true); 
			 cl.setStatut("libre"); 
			 Optional<TypeVehicule> typee = typeRepository.findByType(type);
			 cl.setTypeVehicule(typee.get());
			 userRepository.save(cl);
			 Optional<List<User_IntervalleDistance>> userInts = userDistRepository.findByUser(client.get()); 
			 List<User_IntervalleDistance> ints = userInts.get(); 
			 userDistRepository.deleteAll(ints); 
			 List<IntervalleDistance> intsDist = intDistRepository.findAll(); 
			 User_IntervalleDistance userDist = new User_IntervalleDistance(); 
			 userDist.setIntervalleDistance(intsDist.get(0)); 
			 userDist.setUser(client.get());
			 userDist.setTarif(Long.parseLong(tarif));
			 userDistRepository.save(userDist);
			 User_IntervalleDistance userDistt = new User_IntervalleDistance(); 
			 userDistt.setIntervalleDistance(intsDist.get(1)); 
			 userDistt.setUser(client.get());
			 userDistt.setTarif(Long.parseLong(tariff));
			 userDistRepository.save(userDistt);
			 User_IntervalleDistance userDisttt = new User_IntervalleDistance(); 
			 userDisttt.setIntervalleDistance(intsDist.get(2)); 
			 userDisttt.setUser(client.get());
			 userDisttt.setTarif(Long.parseLong(tarifff));
			 userDistRepository.save(userDisttt);
			 User_IntervalleDistance userDistttt = new User_IntervalleDistance(); 
			 userDistttt.setIntervalleDistance(intsDist.get(3)); 
			 userDistttt.setUser(client.get());
			 userDistttt.setTarif(Long.parseLong(tariffff));
			 userDistRepository.save(userDistttt);
	 }
	 
	 @PostMapping("ajouterLiv") 
	 public Map<String, Object> ajouterLiv(@RequestPart("email") String em, @RequestPart("password") String password, @RequestPart("nom") String nom, @RequestPart("prenom") String prenom, @RequestPart("genre") String genre, @RequestPart("tel") String tel, @RequestPart("type") String type, @RequestPart("token") String token, @RequestPart(name="photo", required = false) MultipartFile photo, @RequestPart("tarif0") String tarif, @RequestPart("tarif1") String tariff, @RequestPart("tarif2") String tarifff, @RequestPart("tarif3") String tariffff) throws IOException 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findByEmail(em);  
			 if (!livreur.isPresent()) 
			 {
				 User liv = new User(); 
				 liv.setEmail(em);
				 BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
				 liv.setPassword(passwordEncoder.encode(password));
				 liv.setNom(nom);
				 liv.setPrenom(prenom); 
				 liv.setGenre(genre); 
				 liv.setActivation(true); 
				 liv.setStatut("libre");
				 Optional<Role> rl = roleRepository.findRoleByRole("Livreur"); 
				 liv.setRole(rl.get()); 
				 Optional<TypeVehicule> ty = vehiculeRepository.findByType(type); 
				 liv.setTypeVehicule(ty.get()); 
				 liv.setTel(Long.parseLong(tel));
				 if (photo != null)
					 liv.setPhoto(photo.getBytes());
				 userRepository.save(liv); 
				 List<IntervalleDistance> intsDist = intDistRepository.findAll(); 
				 User_IntervalleDistance userDist = new User_IntervalleDistance(); 
				 userDist.setIntervalleDistance(intsDist.get(0)); 
				 userDist.setUser(liv);
				 userDist.setTarif(Long.parseLong(tarif));
				 userDistRepository.save(userDist);
				 User_IntervalleDistance userDistt = new User_IntervalleDistance(); 
				 userDistt.setIntervalleDistance(intsDist.get(1)); 
				 userDistt.setUser(liv);
				 userDistt.setTarif(Long.parseLong(tariff));
				 userDistRepository.save(userDistt);
				 User_IntervalleDistance userDisttt = new User_IntervalleDistance(); 
				 userDisttt.setIntervalleDistance(intsDist.get(2)); 
				 userDisttt.setUser(liv);
				 userDisttt.setTarif(Long.parseLong(tarifff));
				 userDistRepository.save(userDisttt);
				 User_IntervalleDistance userDistttt = new User_IntervalleDistance(); 
				 userDistttt.setIntervalleDistance(intsDist.get(3)); 
				 userDistttt.setUser(liv);
				 userDistttt.setTarif(Long.parseLong(tariffff));
				 userDistRepository.save(userDistttt);
				 Optional<Privilege> priv = privilegeRepository.findByPrivilege("livraison"); 
				 User_Privilege userPrivv = new User_Privilege(); 
				 userPrivv.setPrivilege(priv.get()); 
				 userPrivv.setUser(liv); 
				 userPrivRepository.save(userPrivv); 
				 map.put("reponse", liv.getId()); 
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("ajouterCl") 
	 public Map<String, Object> ajouterClient(@RequestPart("email") String em, @RequestPart("password") String password, @RequestPart("nom") String nom, @RequestPart("prenom") String prenom, @RequestPart("genre") String genre, @RequestPart("tel") String tel, @RequestPart("adresse") String adresse, @RequestPart("token") String token, @RequestPart(name="photo", required = false) MultipartFile photo) throws IOException 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findByEmail(em);  
			 if (!client.isPresent()) 
			 {
				 User cl = new User(); 
				 cl.setEmail(em);
				 BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
				 cl.setPassword(passwordEncoder.encode(password));
				 cl.setNom(nom);
				 cl.setPrenom(prenom); 
				 cl.setGenre(genre); 
				 cl.setAdresse(adresse);
				 Optional<Role> rl = roleRepository.findRoleByRole("Client"); 
				 cl.setRole(rl.get());  
				 cl.setTel(Long.parseLong(tel));
				 if (photo != null)
					 cl.setPhoto(photo.getBytes());
				 userRepository.save(cl); 
				 Optional<Privilege> privi = privilegeRepository.findByPrivilege("demande et suivi"); 
				 User_Privilege userPrivilege = new User_Privilege(); 
				 userPrivilege.setPrivilege(privi.get());
				 userPrivilege.setUser(cl); 
				 userPrivRepository.save(userPrivilege); 
				 map.put("reponse", true); 
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("modifierLiv") 
	 public Map<String, Object> modifierLiv(@RequestPart("id") String id, @RequestPart("nom") String nom, @RequestPart("prenom") String prenom, @RequestPart("tel") String tel, @RequestPart("type") String type, @RequestPart("token") String token, @RequestPart("tarif0") String tarif, @RequestPart("tarif1") String tariff, @RequestPart("tarif2") String tarifff, @RequestPart("tarif3") String tariffff) throws IOException 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion et demandes livreurs"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
			 if (livreur.isPresent()) 
			 {
				 if (livreur.get().getStatut().equals("non libre")) 
					 map.put("reponse", "livreur nb"); 
				 else 
				 {
					 Optional<List<User_Livraison>> list = userLivRepository.findByUser(livreur.get());
					 boolean test = false; 
					 for(User_Livraison liv : list.get()) 
					 {
						 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
						 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
						 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
						 {
							 test = true; 
							 break;  
						 }
					 }
					 if (test) 
						 map.put("reponse", "client nb"); 
					 else 
					 {
						 		 User liv = livreur.get();
								 liv.setNom(nom);
								 liv.setPrenom(prenom); 
								 Optional<TypeVehicule> ty = vehiculeRepository.findByType(type); 
								 liv.setTypeVehicule(ty.get()); 
								 liv.setTel(Long.parseLong(tel)); 
								 userRepository.save(liv); 
								 Optional<List<User_IntervalleDistance>> lt = userDistRepository.findByUser(liv);
								 List<User_IntervalleDistance> intsDist = lt.get().stream()
										    .sorted(Comparator.comparingLong(User_IntervalleDistance::getId))
										    .collect(Collectors.toList());
								 intsDist.get(0).setTarif(Long.parseLong(tarif));
								 userDistRepository.save(intsDist.get(0));
								 intsDist.get(1).setTarif(Long.parseLong(tariff));
								 userDistRepository.save(intsDist.get(1));
								 intsDist.get(2).setTarif(Long.parseLong(tarifff));
								 userDistRepository.save(intsDist.get(2));
								 intsDist.get(3).setTarif(Long.parseLong(tariffff));
								 userDistRepository.save(intsDist.get(3));
								 map.put("reponse", true);
					 }
				 }
				  
			 }
			 else 
				 map.put("reponse", "livreur"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 
	 
	 
	 @PostMapping("modifierCl") 
	 public Map<String, Object> modifierCl(@RequestPart("id") String id, @RequestPart("nom") String nom, @RequestPart("prenom") String prenom, @RequestPart("tel") String tel, @RequestPart("adresse") String adresse, @RequestPart("token") String token) throws IOException 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("gestion clients"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 Optional<User> client = userRepository.findById(Long.parseLong(id));
			 if (client.isPresent()) 
			 {
				 User cl = client.get(); 
				 if (client.get().getStatut()!= null && client.get().getStatut().equals("non libre")) 
					 map.put("reponse", "livreur nb"); 
				 else 
				 {
					 Optional<List<User_Livraison>> list = userLivRepository.findByUser(client.get());
					 boolean test = false; 
					 for(User_Livraison liv : list.get()) 
					 {
						 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
						 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
						 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
						 {
							 test = true; 
							 break;  
						 }
					 }
					 if (test) 
						 map.put("reponse", "client nb"); 
					 else 
					 {
						 cl.setNom(nom);
						 cl.setPrenom(prenom);  
						 cl.setTel(Long.parseLong(tel)); 
						 cl.setAdresse(adresse);
						 userRepository.save(cl); 
						 map.put("reponse", true);
					 }
				 } 
			 }
			 else 
				 map.put("reponse", "client"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 
	 @PostMapping("ajouterAdresse") 
	 public void ajouterAdr(@RequestPart("adresse") String adr, @RequestPart("id") String id)
	 {
		 Optional<User> livreur = userRepository.findById(Long.parseLong(id)); 
		 User liv = livreur.get(); 
		 liv.setAdresse(adr); 
		 userRepository.save(liv); 
	 }
	 
	 @PostMapping("fdClients") 
	 public List<Map<String, Object>> findCts(@RequestBody String token) 
	 { 
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("demande et suivi");
		 Optional<List<User_Privilege>> cltss = userPrivRepository.findByPrivilege(privilege.get());
		 List<User_Privilege> clients = cltss.get().stream()
				    .sorted(Comparator.comparingLong(User_Privilege::getId).reversed())
				    .collect(Collectors.toList());
		 List<Map<String, Object>> clts = new LinkedList<>(); 
		 for(User_Privilege client : clients)
		 {
			 Map<String, Object> mp = new HashMap<>(); 
			 mp.put("client", client.getUser());
			 Optional<List<User_Livraison>> list = userLivRepository.findByUser(client.getUser());
			 if (client.getUser().getStatut() != null && client.getUser().getStatut().equals("non libre")) 
				 mp.put("lb", false); 
			 else 
			 {
				 boolean ok = false; 
				 for(User_Livraison liv : list.get()) 
				 {
					 String sql = "select * from historique_etat h, etat e where h.etat_id = e.id and h.livraison_id = ? order by time DESC limit 1"; 
					 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.getLivraison().getId()); 
					 if (sessionRows.get(0).get("etat").toString().equals("acceptée") || sessionRows.get(0).get("etat").toString().equals("colis récupéré")) 
					 {
						 ok = true; 
						 break;  
					 }
				 }
				 if (ok) 
					 mp.put("lb", false); 
				 else 
					 mp.put("lb", true); 
			 }
			 Optional<List<User_Privilege>> userPrivs = userPrivRepository.findByUser(client.getUser());
			 if (userPrivs.get().size() == 6) 
				 mp.put("pr", false); 
			 else 
				 mp.put("pr", true);
			 if (userPrivs.get().size() == 1) 
				 mp.put("prDt", false); 
			 else 
				 mp.put("prDt", true);
			 Optional<List<Abonnement>> ab = abonnementRepository.findByUser(client.getUser()); 
			 if (ab.get().size() > 0) 
			 {
				 List<Abonnement> abs = ab.get();
				 if (abs.get(abs.size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
					 mp.put("ab", false);  
				 else 
					 mp.put("ab", true);
			 }
			 else 
				 mp.put("ab", true); 
			 clts.add(mp); 
		 }  
		 
		 return clts; 
	 }
	 
	 @PostMapping("abns") 
	 public List<Map<String, Object>> findAbonnements(@RequestPart("token") String token) 
	 {
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<List<Abonnement>> abs = abonnementRepository.findByUser(user.get()); 
	     DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		 List<Map<String, Object>> abns = new LinkedList<>(); 
		 for(Abonnement ab : abs.get()) 
		 {
			 Map<String, Object> m = new HashMap<>(); 
			 m.put("id", ab.getId()); 
			 m.put("debut", ft.format(ab.getDateDeb()));
			 m.put("fin", ft.format(ab.getDateFin())); 
			 if (ab.getDateFin().isBefore(LocalDateTime.now())) 
				 m.put("etat", "expiré"); 
			 else 
				 m.put("etat", "en cours"); 
			 abns.add(m); 
		 }	 
		 return abns; 
	 }
	 

	 
	 
	 
	 
	 
	 
	

	
	
	
	
}
