package com.progresee.app.services.dao;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.progresee.app.beans.User;

public interface UserService {

	public ResponseEntity<Object> getUser(String token);


	public ResponseEntity<Object> getClassroom(String token, String classroomId);

	public Map<String, Object> getClassrooms(String token);

	public ResponseEntity<Object> createClassroom(String token, String classroomName, String description);

	public ResponseEntity<Object> updateClassroom(String token, String classroomId, String classroomName,
			String description);

	public ResponseEntity<Object> deleteClassroom(String token, String classroomId);

	public Map<String, Object> getUsersInClassroom(String token, String classroomId);

	public ResponseEntity<Object> transferClassroom(String token, String classroomId, String newOwnerId);

	public ResponseEntity<Object> addToClassroom(String token, String classroomId, String userId);

	public ResponseEntity<Object> leaveClassroom(String token, String classroomId);

	public Map<String, Object> findCurrentUser(String token);

	public ResponseEntity<Object> removeFromClassroom(String token, String classroomId, String userId);

}
