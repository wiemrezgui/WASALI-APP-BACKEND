package com.example.model;

import java.time.LocalDate;

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
@Table(name = "user_livraison")
public class User_Livraison {
	 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id;
	
	@JoinColumn(name = "user_id", columnDefinition = "BIGINT")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	private User user;



	@JoinColumn(name = "livraison_id", columnDefinition = "BIGINT")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	private Livraison livraison;
	 
	
	private LocalDate date; 
	
	public User_Livraison(User user, Livraison livraison, LocalDate date) {
		super();
		this.user = user;
		this.livraison = livraison;
		this.date = date;
	}
	  
}
