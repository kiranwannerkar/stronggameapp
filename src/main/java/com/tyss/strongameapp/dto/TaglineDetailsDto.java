package com.tyss.strongameapp.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TaglineDetailsDto {

	private int taglineDetailsId;

	private String tagline;

	private int userCount;

	private List<String> userImages = new ArrayList<>(3);

}
