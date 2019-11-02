package com.progresee.app.beans;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFinished {

	
	private boolean hasFinished;
	
	private String timestamp;
}
