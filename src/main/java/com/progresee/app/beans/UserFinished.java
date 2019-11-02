package com.progresee.app.beans;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFinished {
	
	private String uid;

	private boolean hasFinished;

	private String timestamp;
	
	private String exerciseUid;
}
