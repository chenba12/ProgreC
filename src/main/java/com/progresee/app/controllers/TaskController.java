package com.progresee.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.progresee.app.beans.Task;
import com.progresee.app.services.TaskServiceImpl;
import com.progresee.app.utils.NullCheckerUtils;

@RestController
@RequestMapping("/task")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

	@Autowired
	private TaskServiceImpl taskService;

	// http://localhost:5000/task/getAll?{classroomId}
	@GetMapping("/getAllTasks")
	public Map<String, Object> getAllTasks(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId) {
		return taskService.getAllTasks(token, classroomId);
	}

	// http://localhost:5000/task/getTask?{classroomId}/{taskId}
	@GetMapping("/getTask")
	public Map<String, Object> getTask(@RequestHeader("Authorization") String token, @RequestParam String classroomId,
			@RequestParam String taskId) {
		return taskService.getTask(token, classroomId, taskId);
	}

	// http://localhost:5000/task/createTask?{classroomId}
	@PostMapping("/createTask")
	public Map<String, Object> createTask(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String title,@RequestParam String description,@RequestParam String link,@RequestParam String date) {
			return taskService.createTask(token, classroomId,title,description,link,date);

	}

	// http://localhost:5000/task/delete?{classroomId}
	@DeleteMapping("/deleteTask")
	public Map<String, Object> deleteTask(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String taskId) {
		return taskService.deleteTask(token, classroomId, taskId);
	}

	// http://localhost:5000/task/update?{classroomId}
	@PutMapping("/updateTask")
	public Map<String, Object> updateTask(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestBody Task task) {
		if (NullCheckerUtils.taskNullChecker(task)) {
			return taskService.updateTask(token, classroomId, task);
		}
		return null;
	}

}
