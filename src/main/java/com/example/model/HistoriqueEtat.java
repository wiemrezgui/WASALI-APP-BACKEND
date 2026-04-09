package com.example.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "historique_etat")
public class HistoriqueEtat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id;
	
	@JoinColumn(name = "livraison_id", columnDefinition = "BIGINT")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne 
	private Livraison livraison; 
	
	@JoinColumn(name = "etat_id", columnDefinition = "BIGINT")
	@ManyToOne 
	private Etat etat; 
	
	private LocalDateTime time;
	

	public HistoriqueEtat(Livraison livraison, Etat etat, LocalDateTime time) {
		super();
		this.livraison = livraison;
		this.etat = etat;
		this.time = time;
	} 
	
	
	

}
