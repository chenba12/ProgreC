package com.progresee.app.beans;

import com.google.cloud.firestore.FieldValue;
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
	private String profileUrl;
	private String provider;
	private FieldValue dateCreated;
	private FieldValue signedIn;
	

}
