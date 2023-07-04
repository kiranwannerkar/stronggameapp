package com.tyss.strongameapp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "transformation_image")
public class TransformationImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7679614704951711401L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transformation_image_id")
	private int transformationImageId;

	@Column(name = "transformation_image")
	private String transformationImageUrl;

}
