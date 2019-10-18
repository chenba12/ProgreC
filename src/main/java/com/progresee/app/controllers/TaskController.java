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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.progresee.app.beans.Task;
import com.progresee.app.services.TaskServiceImpl;
import com.progresee.app.utils.NullCheckerUtils;

@RestController
@RequestMapping("/task")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

	@Autowired
	private TaskServiceImpl taskService;

	// http://localhost:5000/task/getAll?{classRoomId}
	@GetMapping("/getAll")
	public Map<String, Object> getAllTasks(@RequestHeader("Authorization") String token,
			@RequestParam String classRoomId) {
		return taskService.getAllTasks(token, classRoomId);
	}

	// http://localhost:5000/task/getTask?{classRoomId}/{taskId}
	@GetMapping("/getTask")
	public Map<String, Object> getTask(@RequestHeader("Authorization") String token, @RequestParam String classRoomId,
			@RequestParam String taskId) {
		return taskService.getTask(token, classRoomId, taskId);
	}

	// http://localhost:5000/task/createTask?{classRoomId}
	@PostMapping("/createTask")
	public Map<String, Object> createTask(@RequestHeader("Authorization") String token,
			@RequestParam String classRoomId, @RequestBody Task task) {
		if (NullCheckerUtils.taskNullChecker(task)) {
			return taskService.createTask(token, classRoomId, task);
		}
		return null;

	}

	// http://localhost:5000/task/delete?{classRoomId}
	@DeleteMapping("/deleteTask")
	public Map<String, Object> deleteTask(@RequestHeader("Authorization") String token,
			@RequestParam String classRoomId, @RequestParam String taskId) {
		return taskService.deleteTask(token, classRoomId, taskId);
	}

	// http://localhost:5000/task/update?{classRoomId}
	@PutMapping("/update")
	public Map<String, Object> updateTask(@RequestHeader("Authorization") String token,
			@RequestParam String classRoomId, @RequestBody Task task) {
		if (NullCheckerUtils.taskNullChecker(task)) {
			return taskService.updateTask(token, classRoomId, task);
		}
		return null;
	}

	// http://localhost:5000/task/update?{classRoomId}?{taskId}
	@PutMapping("/updateImage")
	public Map<String, Object> updateTaskImage(@RequestHeader("Authorization") String token,
			@RequestParam String classRoomId, @RequestParam String taskId, @RequestPart MultipartFile file) {
		return taskService.updateTaskImage(token, classRoomId, taskId);
	}
}
