package com.progresee.app.beans;

import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private long Id;
	private String uid;
	private String email;
	private String fullName;
	private String pictureURL;
	private Role role;
	private Map<Long, Classroom> classrooms;
	private LocalDateTime dateCreated;
	private LocalDateTime lastLoggedIn;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return Id;
	}

	@Column(unique = true)
	public String getUid() {
		return uid;
	}

	@Column(unique = true, nullable = false, length = 255)
	public String getEmail() {
		return email;
	}

	@Column(nullable = false, length = 100)
	public String getFullName() {
		return fullName;
	}


	@Enumerated(EnumType.STRING)
	public Role getRole() {
		return role;
	}

	@Column
	public LocalDateTime getLastLoggedIn() {
		return lastLoggedIn;
	}

	@Column(nullable = false)
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	@Column
	public String getPictureURL() {
		return pictureURL;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	public Map<Long, Classroom> getClassrooms() {
		return classrooms;
	}

}
