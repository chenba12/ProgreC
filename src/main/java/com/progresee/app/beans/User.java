package com.progresee.app.beans;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	private String uid;
	private String email;
	private String fullName;
	private String profilePictureUrl;
	private Date dateCreated;
	private Date signedIn;

}
