package com.progresee.app.services;

import java.time.LocalDateTime;
import java.util.Hashtable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Completed;
import com.progresee.app.beans.Exercise;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.User;
import com.progresee.app.repositories.CompletedRepository;
import com.progresee.app.repositories.ExerciseRepository;
import com.progresee.app.repositories.TaskRepository;
import com.progresee.app.repositories.UserRepository;
import com.progresee.app.utils.ErrorUtils;


@Service
public class ExerciseService {

	

	@Autowired
	private UserService userService;

	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> ExerciseService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> ExerciseService");
	}

	public ResponseEntity<Object> getAllExercises(String token, long classRoomId, long taskId) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getTasks() != null) {
						Task task = classRoom.getTasks().get(taskId);
						if (task != null) {
							if (task.getExercises() != null && task.getExercises().size() > 0) {
								return ResponseEntity.ok(task.getExercises());
							}
							ResponseEntity.badRequest().body("No exercises found");
						}
					}
					return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> getExercise(String token, long classRoomId, long taskId, long exerciseId) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getTasks() != null) {
						Task task = classRoom.getTasks().get(taskId);
						if (task != null) {
							Exercise exercise = task.getExercises().get(exerciseId);
							if (exercise != null) {
								return ResponseEntity.ok(exercise);
							}
							return ResponseEntity.badRequest()
									.body(ErrorUtils.EXERCISE_ID_NOT_FOUND + exerciseId);
						}
					}
					return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> getFinishedUsers(String token, long classRoomId, long taskId, long exerciseId) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						if (classRoom.getId() == classRoomId)
							if (classRoom.getTasks() != null) {
								Task task = classRoom.getTasks().get(taskId);
								if (task != null) {
									Exercise exercise = task.getExercises().get(exerciseId);
									if (exercise != null) {
										if (exercise.getFinishedUsers() != null
												&& exercise.getFinishedUsers().size() > 0) {
											return ResponseEntity.ok(exercise.getFinishedUsers());
										}
									}
									return ResponseEntity.badRequest()
											.body(ErrorUtils.EXERCISE_ID_NOT_FOUND + exerciseId);
								}
							}
						return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> createExercise(String token, long classRoomId, long taskId, Exercise exercise) {
		exercise.setTaskId(taskId);
		exercise.setFinishedUsers(new Hashtable<>());
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
							Task task = classRoom.getTasks().get(taskId);
							if (task != null) {
								if (task.getExercises() != null) {
									long exId = exerciseRepository.save(exercise).getId();
									task.getExercises().put(exId, exercise);
									taskRepository.save(task);
									userRepository.save(user);
									return ResponseEntity.ok(exercise);
								}
							}
						}
					}
					return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
				}
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}

		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);

	}

	public ResponseEntity<Object> deleteExercise(String token, long classRoomId, long taskId, long exerciseId) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
							Task task = classRoom.getTasks().get(taskId);
							if (task != null) {
								if (task.getExercises() != null && task.getExercises().size() > 0) {
									if (task.getExercises().containsKey(exerciseId)) {
										task.getExercises().remove(exerciseId);
										exerciseRepository.deleteById(exerciseId);
										taskRepository.save(task);
										userRepository.save(user);
										return ResponseEntity.ok("Exercise has been deleted");
									}
								}
								return ResponseEntity.badRequest()
										.body(ErrorUtils.EXERCISE_ID_NOT_FOUND + exerciseId);
							}
						}
						return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);

	}

	public ResponseEntity<Object> updateExercise(String token, long classRoomId, long taskId, Exercise exercise) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
							Task task = classRoom.getTasks().get(taskId);
							if (task != null) {
								if (task.getExercises() != null && task.getExercises().size() > 0) {
									Exercise tempExercise = task.getExercises().get(exercise.getId());
									if (tempExercise != null) {
										exercise.setFinishedUsers(tempExercise.getFinishedUsers());
										task.getExercises().put(tempExercise.getId(), exercise);
										exerciseRepository.save(exercise);
										taskRepository.save(task);
										return ResponseEntity.ok(exercise);
									}
									return ResponseEntity.badRequest()
											.body(ErrorUtils.EXERCISE_ID_NOT_FOUND + exercise.getId());
								}
							}
						}
						return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> updateExerciseStatus(String token, long classRoomId, long taskId, long exerciseId) {
		Completed completed = new Completed();
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
						Task task = classRoom.getTasks().get(taskId);
						if (task != null) {
							if (task.getExercises() != null && task.getExercises().size() > 0) {
								Exercise exercise = task.getExercises().get(exerciseId);
								if (exercise != null) {
									if (exercise.getFinishedUsers() != null) {
										Completed finished = exercise.getFinishedUsers().get(user.getId());
										if (finished != null) {
											exercise.getFinishedUsers().remove(user.getId());
											completedRepository.delete(finished);
										} else {
											completed.setDateCompleted(LocalDateTime.now());
											completed.setFullName(user.getFullName());
											completedRepository.save(completed);
											exercise.getFinishedUsers().put(user.getId(), completed);
											exerciseRepository.save(exercise);
											return ResponseEntity.ok(exercise);
										}
									}
								}
							}
							return ResponseEntity.badRequest()
									.body(ErrorUtils.EXERCISE_ID_NOT_FOUND + exerciseId);
						}
					}
					return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

}
