package com.progresee.app.beans;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
	
	private String name;
	private String owner;
	private String description;
	private int numberOfTasks;
	private Date dateCreated;
	private int numberOfUsers;
	private Map<String,String> userList = new Hashtable<String, String>();
	private String ownerUid;
	private String uid;

}
