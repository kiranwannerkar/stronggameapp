package com.tyss.strongameapp.dto;

import java.util.Date;

import com.tyss.strongameapp.entity.TransformationDetails;
import com.tyss.strongameapp.entity.UserStepsStats;

import lombok.Data;

@Data
public class UserInformationDto {

	private Double coins;

	private int position;

	private String otp;

	private Date packageExpiryDate;

	private int notificationCount;

	private int userId;

	private String name;

	private Date dateOFBirth;

	private String email;

	private String password;

	private String confirmPassword;

	private long mobileNo;

	private double height;

	private double weight;

	private String gender;

	private String photo;

	private String token;

	private UserStepsStats steps;

	private TransformationDetails trans;

	private String cases;

	private String firebaseToken;

	private String friendRefereneCode;

	private String deviceId;

	private String fbUserId;

}
