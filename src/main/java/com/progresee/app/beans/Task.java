package com.progresee.app.beans;

import com.google.cloud.firestore.FieldValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
	
	private String uid;
	private String title;
	private String description;
	private String imageUrl;
	private FieldValue startDate;
	private FieldValue endDate;
	

}
