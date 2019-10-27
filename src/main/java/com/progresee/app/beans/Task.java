package com.progresee.app.beans;

import java.util.Date;
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
	private String referenceLink;
	private Date startDate;
	private Date endDate;
	private String classroomUid;
	private boolean status;
	

}
