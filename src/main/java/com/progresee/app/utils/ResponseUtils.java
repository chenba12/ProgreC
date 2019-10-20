package com.progresee.app.utils;

import java.util.Hashtable;
import java.util.Map;

public class ResponseUtils {
	public static final String USER_NOT_FOUND = "Could not find user";
	public static final String NOT_PART_OF_CLASSROOM = "You are not a part of this classroom";
	public static final String USER_NOT_PART_OF_CLASSROOM = "User is a not part of this classroom";
	public static final String OWNER = "You are not the owner of the classroom";
	public static final int BAD_REQUEST = 400;
	public static final int FORBIDDEN = 403;

	public static Map<String, Object> generateErrorCode(int errorCode, String desc, String path) {
		Map<String, Object> map = new Hashtable<>();
		map.put("Error code ", errorCode);
		map.put("Error description", desc);
		map.put("Path", path);
		return map;
	}

	public static Map<String, Object> generateSuccessString(String msg) {
		Map<String, Object> map = new Hashtable<>();
		map.put("Message ", msg);
		return map;
	}

}
