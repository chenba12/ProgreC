package com.progresee.app.services.dao;

import java.util.Map;

import com.progresee.app.beans.Task;

public interface TaskService {
	
	public Map<String, Object>getAllTasks (String token,String classroomId);
	public Map<String, Object>getTask (String token,String classroomId,String taskId);
	public Map<String, Object> createTask(String token,String classroomId,Task task);
	public Map<String, Object> deleteTask (String token,String classroomId,String taskId);
	public Map<String, Object>updateTask (String token,String classroomId,Task task);
	//TODO wait for android
	public Map<String, Object>updateTaskImage (String token,String classroomId);


}
