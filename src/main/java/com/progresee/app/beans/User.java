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
public class User  {

	private String email;
	private String fullName;
	private String profilePictureUrl;
	private Date dateCreated;
	private Date signedIn;
	private List<String> classroom=new ArrayList<String>();



}
