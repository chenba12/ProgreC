package com.progresee.app.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFinished {
	
	private String email;
	
	private String uid;

	private String hasFinished;

	private String timestamp;
	
	private String exerciseUid;
}
