package com.progresee.app.beans;

import java.util.Date;

import com.google.cloud.firestore.FieldValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
	
	private String title;
	private String description;
	private String imageUrl;
	private Date startDate;
	private Date endDate;
	

}
