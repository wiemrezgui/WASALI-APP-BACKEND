package com.example.model;





import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity 
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id;  
	
	private String email, password, nom, prenom, genre, statut, adresse, localisation; 
	
	private boolean activation;
	
	

	private Long tel; 
	
	
	private byte[] photo;
	
	private byte[] cin; 
	
	
	@JoinColumn(name = "role_id", columnDefinition = "BIGINT")
	@ManyToOne 
	private Role role;
	
	
	@JoinColumn(name = "type_vehicule_id", columnDefinition = "BIGINT")
	@ManyToOne 
	private TypeVehicule typeVehicule; 

	public User(String email, String password, String nom, String prenom, String genre, String adresse,
			Long tel, Role role) {
		this.email = email;
		this.password = password;
		this.nom = nom;
		this.prenom = prenom;
		this.genre = genre;
		this.adresse = adresse;
		this.tel = tel;
		this.role = role;
	}

	public User(String email, String password, String nom, String prenom, String genre, String adresse,
			Long tel, byte[] photo, Role role) {
		this.email = email;
		this.password = password;
		this.nom = nom;
		this.prenom = prenom;
		this.genre = genre;
		this.adresse = adresse;
		this.tel = tel;
		this.photo = photo;
		this.role = role;
	}
	
	public User(String email, String password, String nom, String prenom, String genre, String adresse, Long tel,
			Role role, TypeVehicule typeVehicule) {
		super();
		this.email = email;
		this.password = password;
		this.nom = nom;
		this.prenom = prenom;
		this.genre = genre;
		this.adresse = adresse;
		this.tel = tel;
		this.role = role;
		this.typeVehicule = typeVehicule;
	}


	public User(String email, String password, String nom, String prenom, String genre, String adresse, Long tel,
			byte[] photo, Role role, TypeVehicule typeVehicule) {
		super();
		this.email = email;
		this.password = password;
		this.nom = nom;
		this.prenom = prenom;
		this.genre = genre;
		this.adresse = adresse;
		this.tel = tel;
		this.photo = photo;
		this.role = role;
		this.typeVehicule = typeVehicule;
	}
}
