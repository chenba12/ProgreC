package com.progresee.app.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldValue;
import com.google.firestore.admin.v1.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

	private String email;
	private String fullName;
	private String profilePictureUrl;
	private Date dateCreated;
	private Date signedIn;



}
