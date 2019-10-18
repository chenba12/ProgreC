package com.progresee.app.utils;

import java.util.Hashtable;
import java.util.Map;

public class ResponseUtils {
	public static final String USER_NOT_FOUND = "Could not find user";
	public static final String CLASSROOM_ID_NOT_FOUND = "Could not find classroom with id ";
	public static final String TASK_ID_NOT_FOUND = "Could not find task with id ";
	public static final String EXERCISE_ID_NOT_FOUND = "Could not find exercise with id ";
	public static final String OWNER = "You are not the owner of the classroom";

	public static Map<String, Object> generateErrorCode(int errorCode, String desc, String path) {
		Map<String, Object> map = new Hashtable<>();
		map.put("Error code ", errorCode);
		map.put("Error description", desc);
		map.put("Path", path);
		return map;
	}

	public static Map<String, Object> generateSuccessString(String msg) {
		Map<String, Object> map = new Hashtable<>();
		map.put("Message ",msg);
		return map;
	}

}
