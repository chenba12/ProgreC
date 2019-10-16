package com.progresee.app.beans;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Completed {
	
	private long Id;
	private String fullName;
	private LocalDateTime dateCompleted;
	
	@Id
	@JsonIgnore
	public Long getId() {
		return Id;
	}
	@Column
	public String getFullName() {
		return fullName;
	}
	
	@Column
	public LocalDateTime getDateCompleted() {
		return dateCompleted;
	}
	
	
}
