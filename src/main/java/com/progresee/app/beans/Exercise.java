package com.progresee.app.beans;

import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
	
	private String uid;
	private String exerciseTitle;
	private Date dateCreated;
	private Map<String, Object> usersFinishedList;
	private String taskUid;

}
