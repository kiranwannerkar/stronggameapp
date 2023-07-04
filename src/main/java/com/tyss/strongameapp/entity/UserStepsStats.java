package com.tyss.strongameapp.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "user_steps_stats")
public class UserStepsStats implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserStepsStats() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "steps_id")
	private int stepId;

	@Column(name = "day")
	private Date day;

	@Column(name = "week")
	private double week;

	@Column(name = "month")
	private double month;

	@Column(name = "distance_km")
	private double distanceInKm;

	@Column(name = "calories_burnt")
	private double caloriesBurent;

	@Column(name = "current_steps")
	private int currentSteps;

	@Column(name = "target_steps")
	private int targetSteps;

	@Column(name = "coins_earned")
	private double coinsEarned;

}
