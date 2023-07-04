package com.tyss.strongameapp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "success_story")
public class SuccessStory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "success_story_id")
	private int successStoryId;

	@Column(name = "story_image")
	private String storyImage;

	@NotNull(message = "Story Coach Name shouldn't be Null")
	@NotBlank(message = "Story Coach Name shouldn't be Blank")
	@Column(name = "coach_name")
	private String coachName;

	@Column(name = "coach_age")
	@Size(min = 18, max = 70, message = "Please Enter Valid Age")
	private int coachAge;

	@Column(name = "story_description", length = 999)
	private String storyDescription;

}
