package com.tyss.strongameapp.dto;

import com.tyss.strongameapp.entity.TransformationDetails;
import com.tyss.strongameapp.entity.UserInformation;

import lombok.Data;

@Data
public class TransformationLikeDetailsDto {

	private int transformationLikeId;

	private boolean like;

	private UserInformation transformationLikeUser;

	private TransformationDetails transformationLike;

	private int totalLikes;

}
