package com.progresee.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Exercise;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.User;
import com.progresee.app.repositories.ClassroomRepository;
import com.progresee.app.repositories.TaskRepository;
import com.progresee.app.repositories.UserRepository;
import com.progresee.app.utils.ErrorUtils;
import java.time.LocalDateTime;
import java.util.Hashtable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;


@Service
public class TaskServiceImpl {

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

	public ResponseEntity<Object> getAllTasks(String token, long classRoomId) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
						return ResponseEntity.ok(classRoom.getTasks());
					}
					return ResponseEntity.badRequest().body("No tasks found");
				}
				return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
			}
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> getTask(String token, long classRoomId, long taskId) {
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
							return ResponseEntity.ok(task);
						}
					}
					return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + taskId);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}
	//TODO .setOpenTasks+1
	public ResponseEntity<Object> createTask(String token, long classRoomId, Task task) {
		task.setImageURL(null);
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			Classroom classRoom = user.getClassrooms().get(classRoomId);
			if (classRoom != null) {
				if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
					task.setStartDate(LocalDateTime.now());
					task.setExercises(new Hashtable<Long, Exercise>());
					long id = taskRepository.save(task).getId();
					classRoom.getTasks().put(id, task);
					classRoomRepository.save(classRoom);
					memebersRepository.save(user);
					return ResponseEntity.ok(task);
				}
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> deleteTask(String token, long classRoomId, long taskId) {
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
								classRoom.getTasks().remove(taskId);
								if (task.getImageURL() != null && task.getImageURL().trim() != "") {
									storageService.deleteFileFromS3Bucket(task.getImageURL());
								}
								taskRepository.delete(task);
								classRoomRepository.save(classRoom);
								memebersRepository.save(user);
								return ResponseEntity.ok(task);
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

	public ResponseEntity<Object> updateTask(String token, long classRoomId, Task task) {
		ResponseEntity<?> response = userService.findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null) {
				Classroom classRoom = user.getClassrooms().get(classRoomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						if (classRoom.getTasks() != null && classRoom.getTasks().size() > 0) {
							Task tempTask = classRoom.getTasks().get(task.getId());
							if (tempTask != null) {
								task.setExercises(tempTask.getExercises());
								taskRepository.save(task);
								classRoom.getTasks().put(task.getId(), task);
								classRoomRepository.save(classRoom);
								memebersRepository.save(user);
								return ResponseEntity.ok(task);
							}
						}
						return ResponseEntity.badRequest().body(ErrorUtils.TASK_ID_NOT_FOUND + task.getId());
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorUtils.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(ErrorUtils.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(ErrorUtils.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> updateTaskImage(String token, long classRoomId, long taskId, MultipartFile file) {
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
								task.setImageURL(storageService.uploadFile(file));
								taskRepository.save(task);
								classRoomRepository.save(classRoom);
								memebersRepository.save(user);
								return ResponseEntity.ok(task);
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

}
