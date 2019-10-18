package com.progresee.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.progresee.app.beans.Task;
import com.progresee.app.services.dao.TaskService;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private UserServiceImpl userService;



	@PostConstruct
	public void initDB() {
		System.out.println("Postconstruct -----> TaskSerivce");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> TaskService");
	}

	@Override
	public Map<String, Object> getAllTasks(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getTask(String token, String classroomId, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> createTask(String token, String classroomId, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> deleteTask(String token, String classroomId, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateTask(String token, String classroomId, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateTaskImage(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}


}