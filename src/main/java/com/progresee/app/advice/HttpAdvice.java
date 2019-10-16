package com.progresee.app.advice;

import java.time.LocalDateTime;

import org.hibernate.exception.DataException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.progresee.app.beans.ApiError;

@ControllerAdvice
public class HttpAdvice {

	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<ApiError> handleEmptyDataConflict(EmptyResultDataAccessException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(apiError, HttpStatus.NOT_FOUND);
	}

	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	@ExceptionHandler({ DataException.class })
	public ResponseEntity<ApiError> handleTooLong(DataException ex) {
		ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getMessage(),ex.getCause(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(apiError, HttpStatus.FORBIDDEN);
	}

}
