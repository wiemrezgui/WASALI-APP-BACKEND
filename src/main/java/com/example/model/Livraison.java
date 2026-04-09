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
@Table(name = "livraison")
public class Livraison {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id;  
	
	
	@JoinColumn(name = "dest_id", columnDefinition = "BIGINT")
	@ManyToOne
	private Adresse adresseDest; 
	
	
	@JoinColumn(name = "dep_id", columnDefinition = "BIGINT")
	@ManyToOne 
	private Adresse adressePickUp;


	public Livraison(Adresse adresseDest, Adresse adressePickUp) {
		super();
		this.adresseDest = adresseDest;
		this.adressePickUp = adressePickUp;
	}


	public Livraison(Adresse adresseDest) {
		super();
		this.adresseDest = adresseDest;
	}
	
	
	
	
}
