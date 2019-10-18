package com.progresee.app.services.dao;

import java.util.Map;

import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.User;


public interface UserService {

	public Map<String, Object> getUser(String token);

	public String updateUser(String token, User user);

	public Map<String, Object> getClassroom(String token, String classroomId);

	public Map<String, Object> getClassrooms(String token);

	public Map<String, Object> createClassroom(String token, String classroomName);

	public String updateClassroom(String token, String classroomId, String classroomName);

	public String deleteClassroom(String token, String classroomId);

	public Map<String, Object> getUsersInClassroom(String token, String classroomId);

	public Map<String, Object> transferClassroom(String token, String newOwnerId, String classroomId);
	
	public Map<String, Object> addToClassroom(String token, String userEmail, String classroomId);
	
	public Map<String, Object> leaveClassroom(String token, String classroomId);
	
	public Map<String, Object> findCurrentUser(String token);

	public Map<String, Object> removeFromClassroom(String token, String classroomId, String userId);

	
}
