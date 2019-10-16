package com.progresee.app.beans;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

	private long Id;
	private String ex;
	private long taskId;
	private Map<Long,Completed> finishedUsers;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return Id;
	}

	@Column(nullable = false, length = 255)
	public String getEx() {
		return ex;
	}

	@Column(nullable=false)
	public long getTaskId() {
		return taskId;
	}

	@OneToMany
	@MapKey(name="id")
	public Map<Long,Completed> getFinishedUsers() {
		return finishedUsers;
	}

}
