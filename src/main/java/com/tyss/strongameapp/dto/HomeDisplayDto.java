package com.tyss.strongameapp.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tyss.strongameapp.entity.AdvertisementInformation;

import lombok.Data;

@Component
@Data
public class HomeDisplayDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private double coins;

	private transient List<TransformationDetailsDto> transformation;

	private transient List<DietPlanDetailsDto> diet;

	private transient List<CoachDetailsDto> coach;

	private transient List<HomeBannerInformationDto> homeBanner;

	private List<AdvertisementInformation> advertisements;

	private String cases;

	public HomeDisplayDto(double coins, List<TransformationDetailsDto> transformation, List<DietPlanDetailsDto> diet,
			List<CoachDetailsDto> coach, List<HomeBannerInformationDto> advertisement) {
		super();
		this.coins = coins;
		this.transformation = transformation;
		this.diet = diet;
		this.coach = coach;
		this.homeBanner = advertisement;
	}

	public HomeDisplayDto() {
		super();
	}

}
