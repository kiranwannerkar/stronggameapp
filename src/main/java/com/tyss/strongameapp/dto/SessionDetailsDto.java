package com.tyss.strongameapp.dto;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionDetailsDto {

	private int sessionId;

	private String sessionLink;

	private String sessionName;

	private Date sessionDate;

	private Time sessionTime;

	private String sessionCoachName;

	private double sessionDuration;

	private int slotsAvailable;

	private String photo;

	private int cases;

	private boolean sessionFlag;

	private boolean isUserSessionMapped;

	private String validationCase;

	private List<String> images;

	private int userCount;

}
