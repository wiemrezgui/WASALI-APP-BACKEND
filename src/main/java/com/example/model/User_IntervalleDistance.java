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
@Table(name = "user_intervalle_distance")
public class User_IntervalleDistance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private long id; 

	 
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "user_id", columnDefinition = "BIGINT")
	@ManyToOne
	private User user; 
	
	
	
	@JoinColumn(name = "intervalle_distance_id", columnDefinition = "BIGINT")
	@ManyToOne 
	private IntervalleDistance intervalleDistance; 
	
	
	private double tarif;
	
	public User_IntervalleDistance(User user, IntervalleDistance intervalleDistance, double tarif) {
		super();
		this.user = user;
		this.intervalleDistance = intervalleDistance;
		this.tarif = tarif;
	}
	
	
}
