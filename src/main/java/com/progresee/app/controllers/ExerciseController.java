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
import com.progresee.app.beans.Exercise;
import com.progresee.app.services.ExerciseServiceImpl;

@RestController
@RequestMapping("/exercise")
@CrossOrigin(origins = "http://localhost:4200")
public class ExerciseController {

	@Autowired
	private ExerciseServiceImpl exerciseService;



	// http://localhost:5000/exercise/getExercise?{classRoomId}?{taskId}?{exerciseId}
	@GetMapping("/getExercise")
	public Map<String, Object> getExercise(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestParam String exerciseId) {
		return exerciseService.getExercise(token,classRoomId, taskId, exerciseId);
	}

	@GetMapping("/getFinishedUsers")
	public Map<String, Object> getFinishedUsers(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestParam String exerciseId) {
		return exerciseService.getFinishedUsers(token,classRoomId, taskId, exerciseId);
	}

	// http://localhost:5000/exercise/getAll?{classRoomId}/{taskId}
	@GetMapping("/getAllExercises")
	public Map<String, Object> getAllExercises(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId) {
		return exerciseService.getAllExercises(token,classRoomId, taskId);
	}

	// http://localhost:5000/exercise/createExercise?{classRoomId}/{taskId}
	@PostMapping("/createExercise")
	public Map<String, Object> createExercise(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestParam String description) {
		return exerciseService.createExercise(token,classRoomId, taskId, description);
	}

	// http://localhost:5000/exercise/deleteExercise?{classRoomId}/{taskId}/{exerciseId}
	@DeleteMapping("/deleteExercise")
	public Map<String, Object> deleteExercise(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestParam String exerciseId) {
		return exerciseService.deleteExercise(token,classRoomId, taskId, exerciseId);
	}

	// http://localhost:5000/exercise/update?{classRoomId}/{taskId}
	@PutMapping("/updateExercise")
	public Map<String, Object> updateExercise(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestBody Exercise exercise) {
		return exerciseService.updateExercise(token,classRoomId, taskId, exercise);
	}

	// http://localhost:5000/exercise/updateStatus?{classRoomId}/{taskId}
	@PutMapping("updateStatus")
	public Map<String, Object> updateStatus(@RequestHeader("Authorization") String token,@RequestParam String classRoomId, @RequestParam String taskId,
			@RequestParam String exerciseId) {
		return exerciseService.updateStatus(token,classRoomId, taskId, exerciseId);
	}

}
