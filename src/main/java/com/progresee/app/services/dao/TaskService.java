package com.progresee.app.services.dao;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.progresee.app.beans.Task;

public interface TaskService {

	public Map<String, Object> getAllTasks(String token, String classroomId);

	public ResponseEntity<Object> getTask(String token, String classroomId, String taskId);

	public ResponseEntity<Object> deleteTask(String token, String classroomId, String taskId);

	public ResponseEntity<Object> updateTask(String token, String classroomId, Task task);

	public ResponseEntity<Object> createTask(String token, String classroomId, String title, String description, String link,
			String date);

}
