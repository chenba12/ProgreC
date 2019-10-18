package com.progresee.app.beans;

import com.google.cloud.firestore.FieldValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
	
	private String uid;
	private String name;
	private String owner;
	private FieldValue dateCreated;

}
