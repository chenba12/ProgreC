package com.progresee.app.services.dao;

import java.util.Map;

import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.User;


public interface UserService {

	public Map<String, Object> getUser(String token);

	public Map<String, Object> updateUser(String token, User user);

	public Map<String, Object> getClassroom(String token, String classroomId);

	public Map<String, Object> getClassrooms(String token);

	public Map<String, Object> createClassroom(String token, String classroomName);

	public Map<String, Object> updateClassroom(String token, String classroomId, String classroomName);

	public Map<String, Object> deleteClassroom(String token, String classroomId);

	public Map<String, Object> getUsersInClassroom(String token, String classroomId);

	public Map<String, Object> transferClassroom(String token, String classroomId, String newOwnerId);
	
	public Map<String, Object> addToClassroom(String token, String classroomId, String userId);
	
	public Map<String, Object> leaveClassroom(String token, String classroomId);
	
	public Map<String, Object> findCurrentUser(String token);

	public Map<String, Object> removeFromClassroom(String token, String classroomId, String userId);

	
}
