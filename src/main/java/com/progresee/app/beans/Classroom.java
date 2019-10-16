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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom{

	private long Id;
	private String name;
	private String owner;
	private Map<Long,Task> tasks;
	private LocalDateTime dateCreated;
	private int openTasks;
	

	@Column(nullable=false)
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public Classroom(String name) {
		setName(name);
		setDateCreated(dateCreated);
	}

	public Classroom(String name, String owner) {
		setName(name);
		setOwner(owner);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return Id;
	}

	@Column(nullable = false,length=60)
	public String getName() {
		return name;
	}

	@Column(nullable = false)
	public String getOwner() {
		return owner;
	}
	@Column
	public int getOpenTasks() {
		return openTasks;
	}

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	public Map<Long,Task> getTasks() {
		return tasks;
	}
	
}
