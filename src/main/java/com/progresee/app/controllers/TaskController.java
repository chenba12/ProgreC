package com.progresee.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Object> getAllTasks(@RequestHeader("Authorization") String token,
			@RequestParam long classRoomId) {
		return taskService.getAllTasks(token, classRoomId);
	}

	// http://localhost:5000/task/getTask?{classRoomId}/{taskId}
	@GetMapping("/getTask")
	public ResponseEntity<Object> getTask(@RequestHeader("Authorization") String token, @RequestParam long classRoomId,
			@RequestParam long taskId) {
		return taskService.getTask(token, classRoomId, taskId);
	}

	// http://localhost:5000/task/createTask?{classRoomId}
	@PostMapping("/createTask")
	public ResponseEntity<Object> createTask(@RequestHeader("Authorization") String token,
			@RequestParam long classRoomId, @RequestBody Task task) {
		if (NullCheckerUtils.taskNullChecker(task)) {
			return taskService.createTask(token, classRoomId, task);
		}
		return ResponseEntity.badRequest().body("Task values cannot be null/empty");

	}

	// http://localhost:5000/task/delete?{classRoomId}
	@DeleteMapping("/deleteTask")
	public ResponseEntity<Object> deleteTask(@RequestHeader("Authorization") String token,
			@RequestParam long classRoomId, @RequestParam long taskId) {
		return taskService.deleteTask(token, classRoomId, taskId);
	}

	// http://localhost:5000/task/update?{classRoomId}
	@PutMapping("/update")
	public ResponseEntity<Object> updateTask(@RequestHeader("Authorization") String token,
			@RequestParam long classRoomId, @RequestBody Task task) {
		if (NullCheckerUtils.taskNullChecker(task)) {
			return taskService.updateTask(token, classRoomId, task);
		}
		return ResponseEntity.badRequest().body("Task values cannot be null/empty");
	}

	// http://localhost:5000/task/update?{classRoomId}?{taskId}
	@PutMapping("/updateImage")
	public ResponseEntity<Object> updateTaskImage(@RequestHeader("Authorization") String token,
			@RequestParam long classRoomId, @RequestParam long taskId, @RequestPart MultipartFile file) {
		return taskService.updateTaskImage(token, classRoomId, taskId, file);
	}
}
