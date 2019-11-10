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
	private String referenceLink;
	private String startDate;
	private String endDate;
	private String classroomUid;
	private boolean status;
	private boolean isArchived;
	

}
