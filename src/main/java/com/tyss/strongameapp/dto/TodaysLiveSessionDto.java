package com.tyss.strongameapp.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class TodaysLiveSessionDto {

	private Date date;

	private List<SessionDetailsDto> sessionList;

	private boolean flag;

	private String cases;

}
