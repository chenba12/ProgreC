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
public class Task {
	
	private String Uid;
	private String title;
	private String description;
	private List<String> imageUrls=new ArrayList<String>();
	private List<String> referenceLinks=new ArrayList<>();
	private Date startDate;
	private Date endDate;
	

}
