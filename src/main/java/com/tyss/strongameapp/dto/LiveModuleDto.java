package com.tyss.strongameapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class LiveModuleDto {

	private int liveModuleId;

	private String liveModuleName;

	private String formateShape;

	private List<SpecializationContentDto> specializationContents;

}
