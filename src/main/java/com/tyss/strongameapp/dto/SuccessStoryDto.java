package com.tyss.strongameapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Data
public class SuccessStoryDto {

	private int successStoryId;
	
	private String storyImage;
	
	@NotNull(message = "Story Coach Name shouldn't be Null")
	@NotBlank(message = "Story Coach Name shouldn't be Blank")
	private String coachName;
	
	@Range(min = 18, max = 70, message = "Please Enter Valid Age")
	private int coachAge;
	
	private String storyDescription;

}
