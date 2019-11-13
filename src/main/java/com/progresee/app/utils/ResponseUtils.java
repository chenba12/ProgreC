package com.progresee.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ResponseUtils {
	public static final String USER_NOT_FOUND = "Could not find user";
	public static final String NOT_PART_OF_CLASSROOM = "You are not a part of this classroom";
	public static final String ERROR = "Error";
	public static final String NO_TASKS_AVAILABLE = "No Tasks Available";
	public static final String NO_EXERCISES_AVAILABLE = "No Exercises Available";
	public static final String USER_NOT_PART_OF_CLASSROOM = "User is a not part of this classroom";
	public static final String OWNER = "You are not the owner of the classroom";
	public static final int BAD_REQUEST = 400;
	public static final int FORBIDDEN = 403;

	public static ResponseEntity<Object> generateErrorCode(int errorCode, String desc, String path) {
		Map<String, Object> map = new HashMap<>();
		map.put("Error code ", errorCode);
		map.put("Error description", desc);
		map.put("Path", path);
		return ResponseEntity.badRequest().body(map);
	}

	public static ResponseEntity<Object> generateSuccessString(String msg) {
		return ResponseEntity.ok().body(msg);
	}

	public static Map<String, Object> generateErrorCodePlural(int errorCode, String desc, String path) {
		Map<String, Object> map = new HashMap<>();
		map.put("Error code ", errorCode);
		map.put("Error description", desc);
		map.put("Path", path);
		return map;
	}

}
