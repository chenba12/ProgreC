package com.progresee.app.beans;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
	private String statusCode;
	private String message;
	private String cause;
	private LocalDateTime date;

	public ApiError(HttpStatus statusCode, String message, Throwable cause, LocalDateTime date) {
		setStatusCode(statusCode);
		setMessage(message);
		setCause(cause);
		setDate(date);
	}

	public ApiError(HttpStatus statusCode, String message, LocalDateTime date) {
		setStatusCode(statusCode);
		setMessage(message);
		setDate(date);
	}
	
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode=statusCode.toString();
	}

	public void setCause(Throwable cause) {
		String[] error = cause.getMessage().split(":");
		this.cause = error[1];
	}

	public void setMessage(String message) {
		if (message.contains("User")) {
			String [] newmessage=message.split("User");
			this.message="No"+newmessage[1];
		}
	}

}