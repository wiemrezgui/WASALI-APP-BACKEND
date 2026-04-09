package com.example.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.JwtTokenProvider;
import com.example.model.Abonnement;
import com.example.model.Etat;
import com.example.model.HistoriqueEtat;
import com.example.model.Livraison;
import com.example.model.Privilege;
import com.example.model.User;
import com.example.model.User_Livraison;
import com.example.model.User_Privilege;
import com.example.repository.AbonnementRepository;
import com.example.repository.EtatRepository;
import com.example.repository.HistoriqueEtatRepository;
import com.example.repository.LivraisonRepository;
import com.example.repository.PrivilegeRepository;
import com.example.repository.UserRepository;
import com.example.repository.User_LivraisonRepository;
import com.example.repository.User_PrivilegeRepository;
import com.example.service.EmailService;

@RestController
@RequestMapping("/api/hist")
public class HistoriqueEtatController {
	
	
	@Autowired
	private UserRepository userRepository; 
	
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired 
	private LivraisonRepository livraisonRepository;
	
	@Autowired 
	private EtatRepository etatRepository;
	
	@Autowired 
	private HistoriqueEtatRepository historiqueEtatRepository;
	
	@Autowired 
	private PrivilegeRepository privilegeRepository;
	
	@Autowired 
	private User_PrivilegeRepository userPrivRepository;
	
	@Autowired 
	private User_LivraisonRepository userLivRepository;
	
	@Autowired 
	private AbonnementRepository abnRepository;
	
	@Autowired 
	private EmailService emailService;
	
	
	
	
	@PostMapping("franchirEtapeLiv") 
	 public Map<String, Object> franchirLiv(@RequestPart("token") String token, @RequestPart("id") String id, @RequestPart("button") String button) 
	 {
		 Map<String, Object> mp = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> livreur = userRepository.findByEmail(email);
		 Optional<Livraison> l = livraisonRepository.findById(Long.parseLong(id));
		 Optional<List<User_Livraison>> userLivs = userLivRepository.findByLivraison(l.get()); 
		 String emailCl = ""; 
		 for(User_Livraison userLiv : userLivs.get()) 
			 if (livreur.get().getId() != userLiv.getUser().getId()) 
			 {
				 emailCl = userLiv.getUser().getEmail(); 
				 break; 
			 }
			 if (button.equals("refuser")) 
			 {
				 Optional<Etat> e = etatRepository.findByEtat("refusée"); 
				 HistoriqueEtat newEtat = new HistoriqueEtat(l.get(), e.get(), LocalDateTime.now());
				 historiqueEtatRepository.save(newEtat); 
				 emailService.senddd("Livraison refusée", emailCl, l.get().getId(), "refusee");   
				 mp.put("reponse", true);
			 }
			 else if (button.equals("annuler"))
			 {
				 Optional<Etat> e = etatRepository.findByEtat("annulée"); 
				 HistoriqueEtat newEtat = new HistoriqueEtat(l.get(), e.get(), LocalDateTime.now());
				 historiqueEtatRepository.save(newEtat);
				 User userr = livreur.get();  
				 userr.setStatut("libre"); 
				 userRepository.save(userr);
				 emailService.senddd("Livraison annulée", emailCl, l.get().getId(), "annulee");
				 mp.put("reponse", true);
			 }
			 else if (button.equals("récupérer"))
			 {
				 Optional<Etat> e = etatRepository.findByEtat("colis récupéré"); 
				 HistoriqueEtat newEtat = new HistoriqueEtat(l.get(), e.get(), LocalDateTime.now());
				 historiqueEtatRepository.save(newEtat);
				 emailService.senddd(emailCl, l.get().getId());
				 mp.put("reponse", true);
			 }
			 else if (button.equals("livrer"))
			 {
				 Optional<Etat> e = etatRepository.findByEtat("effectuée"); 
				 HistoriqueEtat newEtat = new HistoriqueEtat(l.get(), e.get(), LocalDateTime.now());
				 historiqueEtatRepository.save(newEtat);
				 User userr = livreur.get();  
				 userr.setStatut("libre"); 
				 userRepository.save(userr);
				 emailService.senddd("Livraison effectuée", emailCl, l.get().getId(), "effectuee");
				 mp.put("reponse", true);
			 }  
		 return mp; 
	 }
	
	@PostMapping("franchirAccepterLiv") 
	 public Map<String, Object> franchirAccepter(@RequestPart("token") String token, @RequestPart("id") String id) 
	 {
		 Map<String, Object> mp = new HashMap<>(); 
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> livreur = userRepository.findByEmail(email);
		 Optional<Privilege> priv = privilegeRepository.findByPrivilege("livraison"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(livreur.get(), priv.get());
		 if (userPriv.isPresent()) 
		 {
				 Optional<Livraison> l = livraisonRepository.findById(Long.parseLong(id)); 
				 Optional<List<User_Livraison>> userLivs = userLivRepository.findByLivraison(l.get()); 
				 String emailCl = ""; 
				 for(User_Livraison userLiv : userLivs.get()) 
					 if (livreur.get().getId() != userLiv.getUser().getId()) 
					 {
						 emailCl = userLiv.getUser().getEmail(); 
						 break; 
					 }
				 if (l.isPresent()) 
				 {
					 Optional<List<Abonnement>> abs = abnRepository.findByUser(livreur.get()); 
					 if (abs.get().get(abs.get().size() - 1).getDateFin().isAfter(LocalDateTime.now())) 
					 {
						 if (livreur.get().getStatut().equals("libre"))
						 {
							 Optional<Etat> e = etatRepository.findByEtat("acceptée"); 
							 HistoriqueEtat newEtat = new HistoriqueEtat(l.get(), e.get(), LocalDateTime.now()); 
							 historiqueEtatRepository.save(newEtat);
							 User userr = livreur.get();  
							 userr.setStatut("non libre"); 
							 userRepository.save(userr);
							 emailService.senddd("Livraison acceptée", emailCl, l.get().getId(), "acceptee");
							 mp.put("reponse", true); 
						 } 
						 else 
							 mp.put("reponse", "non libre"); 
					 }
				 }
				 else 
					 mp.put("reponse", "livraison"); 
		 } 
		 else 
			 mp.put("reponse", "privilege"); 
		 return mp; 
	 }
	 @PostMapping("historique") 
	 public List<Map<String, Object>> findHistoriqueEtats(@RequestBody String token) 
	 {
		 List<Map<String, Object>> livsEtats = new LinkedList<>();
		 String email = jwtTokenProvider.getEmail(token); 
		 Optional<User> user = userRepository.findByEmail(email); 
		 Optional<Privilege> privilege = privilegeRepository.findByPrivilege("CRUD livraisons"); 
		 Optional<User_Privilege> userPriv = userPrivRepository.findByUserAndPrivilege(user.get(), privilege.get()); 
		 if (userPriv.isPresent()) 
		 {
			 List<Livraison> list = livraisonRepository.findAll(); 
			 list = list.stream()
					    .sorted(Comparator.comparingLong(Livraison::getId).reversed())
					    .collect(Collectors.toList());
			 for(Livraison l : list) 
			 {
				 Map<String, Object> mp = new HashMap<>();
				 mp.put("id", l.getId()); 
				 Optional<List<HistoriqueEtat>> histEtat = historiqueEtatRepository.findByLivraison(l); 
				 for(HistoriqueEtat histEt : histEtat.get()) 
					 if (histEt.getEtat().getEtat().equals("en attente acceptation")) 
					 {
						 mp.put("declenchement", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateDeclenchement", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute);
					 }
					 else if (histEt.getEtat().getEtat().equals("acceptée")) 
					 {
						 mp.put("accepter", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateAccepter", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("refusée")) 
					 {
						 mp.put("refuser", true);
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateRefuser", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("colis récupéré")) 
					 {
						 mp.put("recuperer", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateRecuperer", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("annulée")) 
					 {
						 mp.put("annuler", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateAnnuler", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("effectuée")) 
					 {
						 mp.put("livrer", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateLivrer", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
				 livsEtats.add(mp); 
			 }
		 }
		 else 
		 {
			 Optional<List<User_Livraison>> list = userLivRepository.findByUser(user.get()); 
			 List<User_Livraison> lt = list.get().stream()
					    .sorted(Comparator.comparingLong(User_Livraison::getId).reversed())
					    .collect(Collectors.toList());
			 for(User_Livraison l : lt) 
			 {
				 Map<String, Object> mp = new HashMap<>();
				 mp.put("id", l.getLivraison().getId());
				 Optional<List<HistoriqueEtat>> histEtat = historiqueEtatRepository.findByLivraison(l.getLivraison());
				 
				 for(HistoriqueEtat histEt : histEtat.get())
				 {
					 if (histEt.getEtat().getEtat().equals("en attente acceptation")) 
					 {
						 mp.put("declenchement", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateDeclenchement", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); ;
					 }
					 else if (histEt.getEtat().getEtat().equals("acceptée")) 
					 {
						 mp.put("accepter", true);
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateAccepter", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("refusée")) 
					 {
						 mp.put("refuser", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateRefuser", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("colis récupéré")) 
					 {
						 mp.put("recuperer", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateRecuperer", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("annulée")) 
					 {
						 mp.put("annuler", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateAnnuler", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
					 else if (histEt.getEtat().getEtat().equals("effectuée")) 
					 {
						 mp.put("livrer", true); 
						 String month, day, hour, minute; 
						 if (histEt.getTime().getDayOfMonth() < 10) 
							 day = "0" + String.valueOf(histEt.getTime().getDayOfMonth());
						 else 
							 day = String.valueOf(histEt.getTime().getDayOfMonth());
						 if (histEt.getTime().getMonthValue() < 10) 
							 month = "0" + String.valueOf(histEt.getTime().getMonthValue());
						 else 
							 month = String.valueOf(histEt.getTime().getMonthValue());
						 if (histEt.getTime().getHour() < 10) 
							 hour = "0" + String.valueOf(histEt.getTime().getHour());
						 else 
							 hour = String.valueOf(histEt.getTime().getHour());
						 if (histEt.getTime().getMinute() < 10) 
							 minute = "0" + String.valueOf(histEt.getTime().getMinute());
						 else 
							 minute = String.valueOf(histEt.getTime().getMinute());
						 mp.put("dateLivrer", day + "/" + month + "/" + histEt.getTime().getYear() +  " " + hour + ":" + minute); 
					 }
				 }
				 livsEtats.add(mp); 
			 }
		 }
		 return livsEtats; 
	 }
}
