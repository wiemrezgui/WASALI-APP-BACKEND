package com.example.model;

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
@Table(name = "adresse")
public class Adresse {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id; 
	
	@JoinColumn(name = "gouv_id", columnDefinition = "BIGINT")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne 
	private Gouvernorat gouv; 
	
	
	private Long codePostal; 
	
	private String region, autre;

	public Adresse(Gouvernorat gouv, Long codePostal, String region, String autre) {
		super();
		this.gouv = gouv;
		this.codePostal = codePostal;
		this.region = region;
		this.autre = autre;
	}

	
	
	

	
	
	
	
	

}
