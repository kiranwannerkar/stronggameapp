package com.tyss.strongameapp.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserStepsStatsDto {

	private int stepId;

	private Date day;

	private double week;

	private double month;

	private double distanceInKm;

	private double caloriesBurent;

	private int currentSteps;

	private int targetSteps;

	private double coinsEarned;

	private int userId;

	private String validationCases;

}
