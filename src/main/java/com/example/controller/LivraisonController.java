package com.example.controller;




import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.model.Adresse;
import com.example.model.Etat;
import com.example.model.Gouvernorat;
import com.example.model.HistoriqueEtat;
import com.example.model.IntervalleDistance;
import com.example.model.Livraison;
import com.example.model.Privilege;
import com.example.model.User;
import com.example.model.User_IntervalleDistance;
import com.example.model.User_Livraison;
import com.example.model.User_Privilege;
import com.example.repository.AdresseRepository;
import com.example.repository.EtatRepository;
import com.example.repository.GouvernoratRepository;
import com.example.repository.HistoriqueEtatRepository;
import com.example.repository.IntervalleDistanceRepository;
import com.example.repository.LivraisonRepository;
import com.example.repository.PrivilegeRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_IntervalleDistanceRepository;
import com.example.repository.User_LivraisonRepository;
import com.example.repository.User_PrivilegeRepository;
import com.example.service.EmailService;
import com.example.service.MapService;

@RestController
@RequestMapping("/api/livraison")
public class LivraisonController {
	 
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired 
	private UserRepository userRepository; 
	
	@Autowired 
	private GouvernoratRepository gouvRepository;  
	
	@Autowired 
	private AdresseRepository adresseRepository; 
	
	@Autowired 
	private LivraisonRepository livraisonRepository; 
	
	@Autowired 
	private User_LivraisonRepository userLivRepository;
	
	@Autowired 
	private EtatRepository etatRepository;
	
	@Autowired 
	private HistoriqueEtatRepository historiqueEtatRepository; 
	
	
	@Autowired 
	private IntervalleDistanceRepository intDistRepository; 
	
	@Autowired
	private PrivilegeRepository privilegeRepository;  
	
	@Autowired
	private User_PrivilegeRepository userPrivRepository; 
	
	@Autowired
	private EmailService emailService; 
	


	

	
	private final MapService mapService = new MapService();  
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	
	@Autowired
	private User_IntervalleDistanceRepository userIntRepository; 
	 
	 
	
	@PostMapping("assigner")
	 public Map<String, Object> assignerLivraison(@RequestBody Map<String, Object> m) {
		Map<String, Object> map = new HashMap<>();
		String emailClient = jwtTokenProvider.getEmail(m.get("token").toString()); 
		Optional<User> client = userRepository.findByEmail(emailClient); 
		Optional<Privilege> priv = privilegeRepository.findByPrivilege("demande et suivi"); 
		Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(client.get(), priv.get()); 
		if (userPriv.isPresent()) 
		{
			Optional<User> livreur = userRepository.findByEmail(m.get("email").toString());
			if (!livreur.isPresent()) 
			{
				 map.put("reponse", "livreur"); 
			}
			else 
			{
				 priv = privilegeRepository.findByPrivilege("livraison"); 
				 userPriv = userPrivRepository.findByUserAndPrivilege(livreur.get(), priv.get()); 
				 if (userPriv.isPresent()) 
				 {
					 Optional<Gouvernorat> gouvr = gouvRepository.findByGouv(m.get("gouv").toString()); 
					 if (!gouvr.isPresent()) 
							map.put("reponse", "gouvDest"); 
					 else 
					 {
							 Optional<Adresse> adresse = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gouvr.get(), m.get("region").toString(), m.get("nom").toString(), Long.parseLong(m.get("codeP").toString())); 
							 if (adresse.isPresent()) 
							 {   
								 if (m.get("adresseDepNom") != null) 
								 {
									 Optional<Gouvernorat> gvr = gouvRepository.findByGouv(m.get("adresseDepGouv").toString()); 
									 if (gvr.isPresent())
									 {
										 Optional<Adresse> a = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvr.get(), m.get("adresseDepRegion").toString(), m.get("adresseDepNom").toString(), Long.parseLong(m.get("adresseDepCodeP").toString()));
										 if (a.isPresent()) 
										 {
											 Livraison liv = new Livraison();
											 liv = new Livraison(adresse.get(), a.get());	  
											 liv = livraisonRepository.save(liv);
											 Optional<Etat> etat = etatRepository.findByEtat("en attente acceptation"); 
											 HistoriqueEtat livraisonEtat = new HistoriqueEtat(liv, etat.get(), LocalDateTime.now());
											 historiqueEtatRepository.save(livraisonEtat); 
											 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now());
											 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now());
											 userLivRepository.save(clientLiv);
											 userLivRepository.save(livreurLiv);
											 emailService.sendd(m.get("email").toString(), String.valueOf(liv.getId())); 
											 map.put("reponse", liv.getId());
										 }
										 else 
										 {
											 Livraison liv = new Livraison();
											 Adresse ad = new Adresse(gvr.get(), Long.parseLong(m.get("adresseDepCodeP").toString()), m.get("adresseDepRegion").toString(), m.get("adresseDepNom").toString());
											 adresseRepository.save(ad); 
											 liv = new Livraison(adresse.get(), ad);	  
											 liv = livraisonRepository.save(liv);
											 Optional<Etat> etat = etatRepository.findByEtat("en attente acceptation"); 
											 HistoriqueEtat livraisonEtat = new HistoriqueEtat(liv, etat.get(), LocalDateTime.now());
											 historiqueEtatRepository.save(livraisonEtat); 
											 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now());
											 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now());
											 userLivRepository.save(clientLiv);
											 userLivRepository.save(livreurLiv);
											 emailService.sendd(m.get("email").toString(), String.valueOf(liv.getId()));
											 map.put("reponse", liv.getId());
										 }
									 }
									 else 
										 map.put("reponse", "gouvDep");	 
								 }
								 else
								 {
									 Optional<Gouvernorat> gvv = gouvRepository.findByGouv(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 4].split(" ")[1]); 
									 if (gvv.isPresent()) 
									 {
										 
										 String adrNom = ""; 
										 for(int i = 0; i < client.get().getAdresse().split(",").length - 6 ; i++) 
											if (i < client.get().getAdresse().split(",").length - 7)
												adrNom += client.get().getAdresse().split(",")[i] + ","; 
											else 
												adrNom += client.get().getAdresse().split(",")[i];
										 Livraison liv = new Livraison(adresse.get());
										 Optional<Adresse> add = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvv.get(), client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 6], adrNom  + "," +  client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 2] + "," + client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 1], Long.parseLong(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 5]));  
										 if (add.isPresent()) 
											 liv.setAdressePickUp(add.get());
										 else  
										 {
											 Adresse addr = new Adresse(gvv.get(), Long.parseLong(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 5]), client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 6], adrNom + "," +  client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 2] + "," + client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 1]); 
											 adresseRepository.save(addr); 
											 liv.setAdressePickUp(addr);
										 }
										 liv = livraisonRepository.save(liv);
										 Optional<Etat> etat = etatRepository.findByEtat("en attente acceptation"); 
										 HistoriqueEtat livraisonEtat = new HistoriqueEtat(liv, etat.get(), LocalDateTime.now());
										 historiqueEtatRepository.save(livraisonEtat); 
										 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now());
										 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now());
										 userLivRepository.save(clientLiv);
										 userLivRepository.save(livreurLiv);
										 emailService.sendd(m.get("email").toString(), String.valueOf(liv.getId()));
										 map.put("reponse", liv.getId());
									 }
									 else 
										 map.put("reponse", "adresseClient");
								 }
								
									 
							 }
							 else 
							 {
								 Adresse ad = new Adresse(gouvr.get(), Long.parseLong(m.get("codeP").toString()), m.get("region").toString(), m.get("nom").toString()); 
								 adresseRepository.save(ad);
								 if (m.get("adresseDepNom") != null) 
								 {
									 Optional<Gouvernorat> gvr = gouvRepository.findByGouv(m.get("adresseDepGouv").toString()); 
									 if (gvr.isPresent()) 
									 {	 
										 Livraison liv = new Livraison();
										 Optional<Adresse> adsse = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvr.get(), m.get("adresseDepRegion").toString(), m.get("adresseDepNom").toString(), Long.parseLong(m.get("adresseDepCodeP").toString())); 
										 if (adsse.isPresent()) 
										 {
											 liv = new Livraison(ad, adsse.get());  
										 } 
										 else 
										 {
											 Adresse adr = new Adresse(gvr.get(), Long.parseLong(m.get("adresseDepCodeP").toString()), m.get("adresseDepRegion").toString(), m.get("adresseDepNom").toString());
											 adresseRepository.save(adr); 
											 liv = new Livraison(ad, adr); 
										 }
										 liv = livraisonRepository.save(liv); 
										 Optional<Etat> etat = etatRepository.findByEtat("en attente acceptation"); 
										 HistoriqueEtat livraisonEtat = new HistoriqueEtat(liv, etat.get(), LocalDateTime.now());
										 historiqueEtatRepository.save(livraisonEtat);
										 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now());
										 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now());
										 userLivRepository.save(clientLiv);
										 userLivRepository.save(livreurLiv);
										 emailService.sendd(m.get("email").toString(), String.valueOf(liv.getId()));
										 map.put("reponse", liv.getId());
									 }
									 else 
										 map.put("reponse", "gouvDep"); 
								 }
								 else
								 {
									 Optional<Gouvernorat> gvv = gouvRepository.findByGouv(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 4].split(" ")[1]);
									 if (gvv.isPresent()) 
									 {
										 String adrNom = ""; 
										 for(int i = 0; i < client.get().getAdresse().split(",").length - 6 ; i++) 
											if (i < client.get().getAdresse().split(",").length - 7)
												adrNom += client.get().getAdresse().split(",")[i] + ","; 
											else 
												adrNom += client.get().getAdresse().split(",")[i];
										 Livraison liv = new Livraison(ad);
										 Optional<Adresse> add = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvv.get(), client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 6],  adrNom + "," +  client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 2] + "," + client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 1], Long.parseLong(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 5]));  
										 if (add.isPresent()) 
											 liv.setAdressePickUp(add.get());
										 else  
										 {
											 Adresse addr = new Adresse(gvv.get(), Long.parseLong(client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 5]), client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 6], adrNom + "," +  client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 2] + "," + client.get().getAdresse().split(",")[client.get().getAdresse().split(",").length - 1]); 
											 adresseRepository.save(addr); 
											 liv.setAdressePickUp(addr);
										 }
										 liv = livraisonRepository.save(liv); 
										 Optional<Etat> etat = etatRepository.findByEtat("en attente acceptation"); 
										 HistoriqueEtat livraisonEtat = new HistoriqueEtat(liv, etat.get(), LocalDateTime.now());
										 historiqueEtatRepository.save(livraisonEtat);
										 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now());
										 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now());
										 userLivRepository.save(clientLiv);
										 userLivRepository.save(livreurLiv);
										 emailService.sendd(m.get("email").toString(), String.valueOf(liv.getId()));
										 map.put("reponse", liv.getId());
									 }
									 else 
										 map.put("reponse", "adresseClient");

								 }
							 }
						 }
				 }
				 else 
					 map.put("reponse", "privilegeLivreur"); 
			} 		
		}
		else 
			map.put("reponse", "privilegeClient"); 
		return map; 
	 }
	
	 @GetMapping("allLivraisons") 
	 public Map<String, Object> findAllLivraisons() 
	 {
		 Map<String, Object> map = new HashMap<>();
		 List<Map<String, Object>> livs = new LinkedList<>();
		 List<Livraison> list = livraisonRepository.findAll();
		 list = list.stream()
				    .sorted(Comparator.comparingLong(Livraison::getId).reversed())
				    .collect(Collectors.toList());
		 for(Livraison l : list) 
		 {
			 double lat2 = Double.valueOf(l.getAdresseDest().getAutre().split(",")[1]);  
			 double lon2 = Double.valueOf(l.getAdresseDest().getAutre().split(",")[2]); 
			 double lat1 = Double.valueOf(l.getAdressePickUp().getAutre().split(",")[l.getAdressePickUp().getAutre().split(",").length - 2]);  
			 double lon1 = Double.valueOf(l.getAdressePickUp().getAutre().split(",")[l.getAdressePickUp().getAutre().split(",").length - 1]);
			 Optional<List<User_Livraison>> usersLivraisons = userLivRepository.findByLivraison(l);
			 User client = new User(); 
			 User livreur = new User(); 
			 if (usersLivraisons.get().get(0).getId() < usersLivraisons.get().get(1).getId())
			 {
				 client = usersLivraisons.get().get(0).getUser(); 
				 livreur = usersLivraisons.get().get(1).getUser(); 
			 }
			 else
			 {
				 client = usersLivraisons.get().get(1).getUser(); 
				 livreur = usersLivraisons.get().get(0).getUser();
			 }
			 Long distance = mapService.calculateDist(lat1, lon1, lat2, lon2);
			 List<IntervalleDistance> ints = intDistRepository.findAll(); 
			 for(IntervalleDistance intDist : ints) 
			 {
				 if (distance >= intDist.getBorneGauche() && distance < intDist.getBorneDroite())
				 {
					 Map<String, Object> m = new HashMap<>();
					 String sql = "select * from livraison l , historique_etat h, etat e where l.id = h.livraison_id and h.etat_id = e.id and l.id = ? order by time DESC limit 1"; 
					 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, l.getId()); 
					 m.put("id", l.getId()); 
					 m.put("etat", sessionRows.get(0).get("etat")); 
					 m.put("adresseDest", l.getAdresseDest().getAutre().split(",")[1] + "," + l.getAdresseDest().getAutre().split(",")[2]);
					 m.put("nom", l.getAdresseDest().getAutre().split(",")[0]); 
					 m.put("region", l.getAdresseDest().getRegion()); 
					 m.put("codeP", l.getAdresseDest().getCodePostal());
					 m.put("gouv", l.getAdresseDest().getGouv().getGouv());
					 m.put("adresseDep", l.getAdressePickUp().getAutre().split(",")[l.getAdressePickUp().getAutre().split(",").length - 2] + "," + l.getAdressePickUp().getAutre().split(",")[l.getAdressePickUp().getAutre().split(",").length - 1]);
					 m.put("depGouv", l.getAdressePickUp().getGouv().getGouv());
					 m.put("depRegion", l.getAdressePickUp().getRegion()); 
					 m.put("depCodeP", l.getAdressePickUp().getCodePostal());
					 String depNom = ""; 
					 for(int i = 0; i < l.getAdressePickUp().getAutre().split(",").length - 2 ; i++) 
						if (i < l.getAdressePickUp().getAutre().split(",").length - 3)
							depNom += l.getAdressePickUp().getAutre().split(",")[i] + ","; 
						else 
							depNom += l.getAdressePickUp().getAutre().split(",")[i]; 
					 m.put("depNom", depNom); 
					 m.put("id", l.getId());
					 String month, day;
					 if (usersLivraisons.get().get(1).getDate().getDayOfMonth() < 10) 
						 day = "0" + String.valueOf(usersLivraisons.get().get(1).getDate().getDayOfMonth());
					 else 
						 day = String.valueOf(usersLivraisons.get().get(1).getDate().getDayOfMonth());
					 if (usersLivraisons.get().get(1).getDate().getMonthValue() < 10) 
						 month = "0" + String.valueOf(usersLivraisons.get().get(1).getDate().getMonthValue());
					 else 
						 month = String.valueOf(usersLivraisons.get().get(1).getDate().getMonthValue());
					 m.put("date", day + "/" + month + "/" + usersLivraisons.get().get(0).getDate().getYear());
					 m.put("nomClient", client.getNom() + " " +  client.getPrenom()); 
					 m.put("telClient", client.getTel()); 
					 m.put("nomLivreur", livreur.getNom() + " " + livreur.getPrenom());
					 m.put("telLivreur", livreur.getTel()); 
					 m.put("idLivreur", livreur.getId());
					 m.put("idClient", client.getId());
					 m.put("photoLivreur", livreur.getPhoto());
					 m.put("cinLivreur", livreur.getCin());
					 m.put("photoClient", client.getPhoto()); 
					 m.put("genreClient", client.getGenre());
					 m.put("genreLivreur", livreur.getGenre());
					 m.put("idVehicule", livreur.getTypeVehicule().getId());
					 Optional<User_IntervalleDistance> userInt = userIntRepository.findByUserAndIntervalleDistance(livreur, intDist);
					 m.put("tarif", userInt.get().getTarif() * distance); 
					 livs.add(m); 
					 break; 
				 }
			 }
			 
			 
		 } 
		 map.put("reponse", livs); 
		 return map; 
	 }
	 
	 @PostMapping("livraisonsUser") 
	 public Map<String, Object> findLivraisonsUser(@RequestBody String token) 
	 {
		 Map<String, Object> map = new HashMap<>();
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email);
		 List<Map<String, Object>> livs = new LinkedList<>();
		 Optional<List<User_Livraison>> list = userLivRepository.findByUser(user.get());
		 List<User_Livraison> lt = list.get().stream()
				    .sorted(Comparator.comparingLong(User_Livraison::getId).reversed())
				    .collect(Collectors.toList());
		 for(User_Livraison l : lt) 
		 {
			 double lat2 = Double.valueOf(l.getLivraison().getAdresseDest().getAutre().split(",")[1]);  
			 double lon2 = Double.valueOf(l.getLivraison().getAdresseDest().getAutre().split(",")[2]); 
			 double lat1 = Double.valueOf(l.getLivraison().getAdressePickUp().getAutre().split(",")[l.getLivraison().getAdressePickUp().getAutre().split(",").length - 2]);  
			 double lon1 = Double.valueOf(l.getLivraison().getAdressePickUp().getAutre().split(",")[l.getLivraison().getAdressePickUp().getAutre().split(",").length - 1]); 
			 Optional<List<User_Livraison>> usersLivraisons = userLivRepository.findByLivraison(l.getLivraison());
			 User client = new User(); 
			 User livreur = new User(); 
			 if (usersLivraisons.get().get(0).getId() < usersLivraisons.get().get(1).getId())
			 {
				 client = usersLivraisons.get().get(0).getUser(); 
				 livreur = usersLivraisons.get().get(1).getUser(); 
			 }
			 else
			 {
				 client = usersLivraisons.get().get(1).getUser(); 
				 livreur = usersLivraisons.get().get(0).getUser();
			 }
			 Long distance = mapService.calculateDist(lat1, lon1, lat2, lon2);
			 List<IntervalleDistance> ints = intDistRepository.findAll(); 
			 for(IntervalleDistance intDist : ints) 
			 {
				 if (distance >= intDist.getBorneGauche() && distance < intDist.getBorneDroite())
				 {
					 Map<String, Object> m = new HashMap<>();
					 String sql = "select * from livraison l , historique_etat h, etat e where l.id = h.livraison_id and h.etat_id = e.id and l.id = ? order by time DESC limit 1"; 
					 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, l.getLivraison().getId()); 
					 m.put("id", l.getLivraison().getId());
					 m.put("etat", sessionRows.get(0).get("etat"));
					 m.put("adresseDest", l.getLivraison().getAdresseDest().getAutre().split(",")[1] + "," + l.getLivraison().getAdresseDest().getAutre().split(",")[2]);
					 m.put("nom", l.getLivraison().getAdresseDest().getAutre().split(",")[0]); 
					 m.put("region", l.getLivraison().getAdresseDest().getRegion()); 
					 m.put("codeP", l.getLivraison().getAdresseDest().getCodePostal());
					 m.put("gouv", l.getLivraison().getAdresseDest().getGouv().getGouv()); 
					 m.put("idLivreur", livreur.getId()); 
					 String month, day;
					 if (usersLivraisons.get().get(1).getDate().getDayOfMonth() < 10) 
						 day = "0" + String.valueOf(usersLivraisons.get().get(1).getDate().getDayOfMonth());
					 else 
						 day = String.valueOf(usersLivraisons.get().get(1).getDate().getDayOfMonth());
					 if (usersLivraisons.get().get(1).getDate().getMonthValue() < 10) 
						 month = "0" + String.valueOf(usersLivraisons.get().get(1).getDate().getMonthValue());
					 else 
						 month = String.valueOf(usersLivraisons.get().get(1).getDate().getMonthValue());
					 m.put("date", day + "/" + month + "/" + usersLivraisons.get().get(1).getDate().getYear()); 
					 m.put("photoLivreur", livreur.getPhoto());
					 m.put("cinLivreur", livreur.getCin());
					 m.put("photoClient", client.getPhoto());
					 m.put("genreClient", client.getGenre());  
					 m.put("genreLivreur", livreur.getGenre());
					 m.put("adresseDep", l.getLivraison().getAdressePickUp().getAutre().split(",")[l.getLivraison().getAdressePickUp().getAutre().split(",").length - 2] + "," + l.getLivraison().getAdressePickUp().getAutre().split(",")[l.getLivraison().getAdressePickUp().getAutre().split(",").length - 1]);
					 m.put("depGouv", l.getLivraison().getAdressePickUp().getGouv().getGouv());
					 m.put("depRegion", l.getLivraison().getAdressePickUp().getRegion()); 
					 m.put("depCodeP", l.getLivraison().getAdressePickUp().getCodePostal());
					 String depNom = ""; 
					 for(int i = 0; i < l.getLivraison().getAdressePickUp().getAutre().split(",").length - 2 ; i++) 
						if (i < l.getLivraison().getAdressePickUp().getAutre().split(",").length - 3)
							depNom += l.getLivraison().getAdressePickUp().getAutre().split(",")[i] + ","; 
						else 
							depNom += l.getLivraison().getAdressePickUp().getAutre().split(",")[i];
					 m.put("depNom", depNom);
					 m.put("nomLivreur", livreur.getNom() + " " + livreur.getPrenom());
					 m.put("telLivreur", livreur.getTel());
					 m.put("nomClient", client.getNom() + " " +  client.getPrenom()); 
					 m.put("telClient", client.getTel());
					 Optional<User_IntervalleDistance> userInt = userIntRepository.findByUserAndIntervalleDistance(livreur, intDist);
					 m.put("tarif", userInt.get().getTarif() * distance); 
					 livs.add(m); 
					 break; 
				 }
			 }
			 
			 
		 } 
		 map.put("reponse", livs); 
		 return map; 
	 }
	 
	 
	 
	 @PostMapping("deleteLiv") 
	 public Map<String, Object> deleteLivraison(@RequestBody Map<String, Object> mp) 
	 {
		 Map<String, Object> map = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(mp.get("token").toString()); 
		 Optional<User> client = userRepository.findByEmail(email); 
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("CRUD livraisons");
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(client.get(), priv.get());
		 if (userPriv.isPresent()) 
		 {
			 Optional<Livraison> l = livraisonRepository.findById(Long.parseLong(mp.get("id").toString())); 
			 if (l.isPresent())
			 {
				 String sql = "select * from livraison l , historique_etat h, etat e where l.id = h.livraison_id and h.etat_id = e.id and l.id = ? order by time DESC limit 1"; 
				 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, l.get().getId());
				 if (sessionRows.get(0).get("etat").equals("colis récupéré") || sessionRows.get(0).get("etat").equals("acceptée")) 
					 map.put("reponse", "en cours"); 
				 else 
				 {
					 livraisonRepository.delete(l.get()); 
					 map.put("reponse", true);
				 } 
			 }
			 else 
				 map.put("reponse", "livraison"); 
		 }
		 else 
			 map.put("reponse", "privilege"); 
		 return map; 
	 }
	 
	 @PostMapping("etatLiv") 
	 public Map<String, Object> etatLivraison(@RequestPart("id") String idLiv){
		 Map<String, Object> m = new HashMap<>(); 
		 Optional<Livraison> liv = livraisonRepository.findById(Long.parseLong(idLiv));
		 String sql = "select * from livraison l , historique_etat h, etat e where l.id = h.livraison_id and h.etat_id = e.id and l.id = ? order by time DESC limit 1"; 
		 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.get().getId());
		 m.put("reponse", sessionRows.get(0).get("etat"));
		 return m; 
		
	 }
	 
	 @PostMapping("updateLiv")
	 public Map<String, Object> updateLivraison(@RequestPart("depNom") String depNom, @RequestPart("depRegion") String depRegion, @RequestPart("depCodeP") String depCodeP, @RequestPart("depGouv") String depGouv, @RequestPart("nom") String nom, @RequestPart("region") String region, @RequestPart("gouv") String gouv, @RequestPart("codeP") String codeP, @RequestPart("idLiv") String idLiv, @RequestPart("idLivreur") String idLivreur, @RequestPart("token") String token)
	 {
		 Map<String, Object> m = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("CRUD livraisons"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get());
		 if (userPriv.isPresent()) 
		 {
			 Long idLivs = Long.parseLong(idLiv); 
			 Long idLv = Long.parseLong(idLivreur); 
			 Optional<Livraison> liv = livraisonRepository.findById(idLivs);
			 if (liv.isPresent()) 
			 {
				 String sql = "select * from livraison l , historique_etat h, etat e where l.id = h.livraison_id and h.etat_id = e.id and l.id = ? order by time DESC limit 1"; 
				 List<Map<String, Object>> sessionRows = jdbcTemplate.queryForList(sql, liv.get().getId());
				 if (sessionRows.get(0).get("etat").equals("colis récupéré") || sessionRows.get(0).get("etat").equals("acceptée")) 
					 m.put("reponse", "en cours");
				 else 
				 {
					 
					 Optional<List<User_Livraison>> userLivsOp = userLivRepository.findByLivraison(liv.get()); 
					 List<User_Livraison> userLivs = userLivsOp.get();  
					 Optional<User> livreur = userRepository.findById(idLv); 
					 if (livreur.isPresent()) 
					 {
							 Adresse dep = null; 
							 Adresse dest = null;
							 Optional<Gouvernorat> gouvr = gouvRepository.findByGouv(gouv);
							 Optional<Gouvernorat> gvr = gouvRepository.findByGouv(depGouv);
							 int p = 0; 
							 @SuppressWarnings("unused")
							 User client = new User(); 
							 if (userLivs.get(0).getId() < userLivs.get(1).getId())
							 {
								 p = 1; 
								 client = userLivs.get(0).getUser();
							 }
							 else
							 {
								 p = 0; 
								 client = userLivs.get(1).getUser(); 
							 }	 
							 if (gvr.isPresent() && gouvr.isPresent())
							 {
								 Long codePos = Long.parseLong(codeP);
								 Optional<Adresse> adDest = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gouvr.get(), region, nom, codePos); 
								 if (adDest.isPresent()) 
									 dest = adDest.get();  	 
								 else 
								 {
									 dest = new Adresse(gouvr.get(), codePos, region, nom); 
									 adresseRepository.save(dest); 
								 }
								 Optional<Adresse> adDep = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvr.get(), depRegion, depNom, Long.parseLong(depCodeP));  
								 if (adDep.isPresent()) 
										dep = adDep.get(); 
								 else 
								 {
										dep = new Adresse(gvr.get(), Long.parseLong(depCodeP), depRegion, depNom);  
										adresseRepository.save(dep); 
								 } 
								 Livraison l = liv.get();
								 l.setAdresseDest(dest);
								 l.setAdressePickUp(dep); 
								 livraisonRepository.save(l); 
								 User livrr = livreur.get(); 
								 userLivs.get(p).setUser(livrr);
								 userLivRepository.save(userLivs.get(p));	 
							 }
							 else if (!gvr.isPresent()) 
								 m.put("reponse", "gouvDep"); 
							 else 
								 m.put("reponse", "gouvDest");
					 }
					 else 
						 m.put("reponse", "livreur"); 
				 }
			 }
			 else 
				 m.put("reponse", "livraison"); 
		 }
		 else
			 m.put("reponse", "privilege"); 
		 return m; 
	 }
	 
	 
	 @PostMapping("insertLiv")
	 public Map<String, Object> insertLivraison(@RequestPart("depNom") String depNom, @RequestPart("depRegion") String depRegion, @RequestPart("depCodeP") String depCodeP, @RequestPart("depGouv") String depGouv, @RequestPart("nom") String nom, @RequestPart("region") String region, @RequestPart("gouv") String gouv, @RequestPart("codeP") String codeP, @RequestPart("idLivreur") String idLivreur, @RequestPart("idClient") String idClient, @RequestPart("token") String token)
	 {
		 Map<String, Object> m = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("CRUD livraisons"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), priv.get());
		 if (userPriv.isPresent()) 
		 {
			 Long idLv = Long.parseLong(idLivreur);
			 Long idCl = Long.parseLong(idClient); 
			 Livraison liv = new Livraison();    
			 Optional<User> livreur = userRepository.findById(idLv); 
			 Optional<User> client = userRepository.findById(idCl); 
			 if (livreur.isPresent()) 
			 {
					 if (client.isPresent()) 
					 {
						 Adresse dep = null; 
						 Adresse dest = null;
						 Optional<Gouvernorat> gouvr = gouvRepository.findByGouv(gouv);
						 Optional<Gouvernorat> gvr = gouvRepository.findByGouv(depGouv);
						 if (gvr.isPresent() && gouvr.isPresent())
						 {
							 Long codePos = Long.parseLong(codeP);
							 Optional<Adresse> adDest = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gouvr.get(), region, nom, codePos); 
							 if (adDest.isPresent()) 
								 dest = adDest.get();  	 
							 else 
							 {
								 dest = new Adresse(gouvr.get(), codePos, region, nom); 
								 adresseRepository.save(dest); 
							 }
							 Optional<Adresse> adDep = adresseRepository.findByGouvAndRegionAndAutreAndCodePostal(gvr.get(), depRegion, depNom, Long.parseLong(depCodeP));  
							 if (adDep.isPresent()) 
									dep = adDep.get(); 
							 else 
							 {
									dep = new Adresse(gvr.get(), Long.parseLong(depCodeP), depRegion, depNom);  
									adresseRepository.save(dep); 
							 } 
							 liv.setAdresseDest(dest);
							 liv.setAdressePickUp(dep); 
							 livraisonRepository.save(liv); 
							 Optional<Etat> et = etatRepository.findByEtat("en attente acceptation"); 
							 HistoriqueEtat h = new HistoriqueEtat(liv, et.get(), LocalDateTime.now());
							 historiqueEtatRepository.save(h); 
							 User_Livraison clientLiv = new User_Livraison(client.get(), liv, LocalDate.now()); 
							 User_Livraison livreurLiv = new User_Livraison(livreur.get(), liv, LocalDate.now()); 
							 userLivRepository.save(clientLiv); 
							 userLivRepository.save(livreurLiv);
							 emailService.sendd(livreur.get().getEmail(), String.valueOf(liv.getId()));
							 emailService.sendd(client.get().getEmail(), String.valueOf(liv.getId()));
							 m.put("reponse", liv.getId()); 
						 }
						 else if (!gvr.isPresent()) 
							 m.put("reponse", "gouvDep"); 
						 else 
							 m.put("reponse", "gouvDest");
					 }
					 else 
						 m.put("reponse", "client");  
			 }
			 else 
				 m.put("reponse", "livreur");
		 }
		 else 
			 m.put("reponse", "privilege");  
		 return m; 
	 }
	 
	
	 
	 
	 
	 
	
	
	
	

}
