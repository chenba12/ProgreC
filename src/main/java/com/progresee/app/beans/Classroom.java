package com.progresee.app.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
	
	private String name;
	private String owner;
	private Date dateCreated;
	private List<String> userList = new ArrayList<String>();
	private String uid;

}
