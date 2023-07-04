package com.tyss.strongameapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleCoachDetailsDto {

	private int coachId;

	private String coachName;

	private List<String> certifications;

	private String coachDetail;

	private long phoneNumber;

	private String emailId;

	private String badge;

	private int experience;

	private int noOfUserTrained;

	private List<String> specializations;

	private String photo;

	private String cases;

	private boolean subscribed;

	private boolean topList;

	private List<String> languages;

	private List<CoachReviewDto> coachReview;

	private String instagramLink;

	private String instagramName;

	private int slotsAvailable;

	private int trained;

	private double coachRatings;

	private List<TransformationDetailsDto> transformations;

}
