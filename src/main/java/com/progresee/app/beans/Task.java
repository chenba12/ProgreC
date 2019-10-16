package com.progresee.app.beans;

import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

	private long Id;
	private String title;
	private String description;
	private String imageURL;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Map<Long,Exercise> exercises;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return Id;
	}

	@Column(nullable=false,length=60)
	public String getTitle() {
		return title;
	}

	@Column(nullable=false,length=255)
	public String getDescription() {
		return description;
	}

	@Column
	public String getImageURL() {
		return imageURL;
	}

	@Column(nullable=false)
	public LocalDateTime getStartDate() {
		return startDate;
	}

	@Column(nullable=false)
	public LocalDateTime getEndDate() {
		return endDate;
	}

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	public Map<Long,Exercise> getExercises() {
		return exercises;
	}

}
