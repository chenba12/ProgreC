package com.progresee.app.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import com.progresee.app.beans.Exercise;
import com.progresee.app.repositories.ExerciseRepository;
import com.progresee.app.services.ExerciseService;

@RestController
@RequestMapping("/exercise")
@CrossOrigin(origins = "http://localhost:4200")
public class ExerciseController {

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseRepository exerciseRepository;

	@GetMapping("/getAll")
	public List<Exercise> findAll() {
		return exerciseRepository.findAll();
	}

	// http://localhost:5000/exercise/getExercise?{classRoomId}?{taskId}?{exerciseId}
	@GetMapping("/getExercise")
	public ResponseEntity<Object> getExercise(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestParam long exerciseId) {
		return exerciseService.getExercise(token,classRoomId, taskId, exerciseId);
	}

	@GetMapping("/getFinishedUsers")
	public ResponseEntity<Object> getFinishedUsers(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestParam long exerciseId) {
		return exerciseService.getFinishedUsers(token,classRoomId, taskId, exerciseId);
	}

	// http://localhost:5000/exercise/getAll?{classRoomId}/{taskId}
	@GetMapping("/getAllExercises")
	public ResponseEntity<Object> getAllExercises(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId) {
		return exerciseService.getAllExercises(token,classRoomId, taskId);
	}

	// http://localhost:5000/exercise/createExercise?{classRoomId}/{taskId}
	@PostMapping("/createExercise")
	public ResponseEntity<Object> createExercise(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestBody Exercise exercise) {
		return exerciseService.createExercise(token,classRoomId, taskId, exercise);
	}

	// http://localhost:5000/exercise/deleteExercise?{classRoomId}/{taskId}/{exerciseId}
	@DeleteMapping("/deleteExercise")
	public ResponseEntity<Object> deleteExercise(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestParam long exerciseId) {
		return exerciseService.deleteExercise(token,classRoomId, taskId, exerciseId);
	}

	// http://localhost:5000/exercise/update?{classRoomId}/{taskId}
	@PutMapping("/update")
	public ResponseEntity<Object> updateExercise(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestBody Exercise exercise) {
		return exerciseService.updateExercise(token,classRoomId, taskId, exercise);
	}

	// http://localhost:5000/exercise/updateStatus?{classRoomId}/{taskId}
	@PutMapping("updateStatus")
	public ResponseEntity<Object> updateExerciseStatus(@RequestHeader("Authorization") String token,@RequestParam long classRoomId, @RequestParam long taskId,
			@RequestParam long exerciseId) {
		return exerciseService.updateExerciseStatus(token,classRoomId, taskId, exerciseId);
	}

}
