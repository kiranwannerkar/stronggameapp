package com.tyss.strongameapp.dto;

import java.util.List;

import com.tyss.strongameapp.entity.TransformationImage;

import lombok.Data;

@Data
public class SingleTransformationDto {

	private int transformationId;

	private String transformationDetail;

	private String transformationVideo;

	private String userName;

	private String coachName;

	private String plan;

	private Integer totalLikes;

	private List<TransformationImage> image;

	private Boolean flag;

	private SingleCoachDetailsDto singleCoach;

	private String cases;

}
