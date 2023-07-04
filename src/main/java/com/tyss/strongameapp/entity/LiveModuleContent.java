package com.tyss.strongameapp.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;

@Data
@Entity
@Table(name = "live_module_content")
public class LiveModuleContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5767392466277360203L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "live_content_id")
	private int liveContentId;

	@Column(name = "live_Content_name", unique = true)
	private String liveContentName;

	@Column(name = "content_description", length = 999)
	private String contentDescription;

	@Column(name = "image_content")
	private String imageContent;

	@Column(name = "video_content")
	private String videoContent;

	@Column(name = "duration")
	private double duration;

	@Column(name = "intensity")
	private String intensity;

	@Column(name = "level")
	private String level;

	@Column(name = "viewer_count")
	private int viewerCount;

	@Exclude
	@JsonManagedReference(value = "livecontent-studiobanner")
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "moduleContent")
	private List<StudioBannerInformation> contentStudioBanner;

	@Exclude
	@JsonBackReference(value = "specialization-livecontent")
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "specialization_content_id")
	private SpecializationContent specializationContent;

	@Exclude
	@JsonManagedReference(value = "studiocontent-streamed")
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "streamStudioContent")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<StreamedStudioContent> studioContentStreamed;
}
