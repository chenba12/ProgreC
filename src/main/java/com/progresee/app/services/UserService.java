package com.progresee.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.api.core.SettableApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Role;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.User;
import com.progresee.app.repositories.ClassroomRepository;
import com.progresee.app.repositories.UserRepository;
import com.progresee.app.utils.BadRequestsResponse;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClassroomRepository classroomRepository;

	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> UserService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> UserService");
	}

	public ResponseEntity<?> findCurrentUser(String token) {
//		final SettableApiFuture<ResponseEntity<?>> future = SettableApiFuture.create();
//		com.google.firebase.tasks.Task<FirebaseToken> decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
//		decodedToken.addOnSuccessListener(new OnSuccessListener<FirebaseToken>() {
//
//			@Override
//			public void onSuccess(FirebaseToken token) {
//				String uid = decodedToken.getResult().getUid();
//				User user = userRepository.findByUid(uid);
//				if (user == null) {
//					user = new User();
//					user.setUid(uid);
//					user.setEmail(decodedToken.getResult().getEmail());
//					user.setFullName(decodedToken.getResult().getName());
//					user.setPictureURL(decodedToken.getResult().getPicture());
//					user.setRole(Role.ROLE_USER);
//					user.setLastLoggedIn(LocalDateTime.now());
//					user.setDateCreated(LocalDateTime.now());
//					future.set(ResponseEntity.ok(userRepository.save(user)));
//				} else {
//					future.set(ResponseEntity.ok(user));
//				}
//			}
//
//		}).addOnFailureListener(new OnFailureListener() {
//
//			@Override
//			public void onFailure(Exception arg0) {
//				future.set(ResponseEntity.badRequest().body("Something went wrong"));
//			}
//		});
//		try {
//			return future.get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
		return null;
	}

	public ResponseEntity<Object> getCurrentUser(String token) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
		}
	}

	public ResponseEntity<Object> getUsers() {
		List<User> users = userRepository.findAll();
		if (users != null && users.size() > 0) {
			return ResponseEntity.ok(users);
		}
		return ResponseEntity.badRequest().body("No users found");
	}

	public ResponseEntity<Object> getUser(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> updateUser(String token, User user) {
		ResponseEntity<?> response = findCurrentUser(token);
		User tempUser = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			tempUser = (User) response.getBody();
			user.setClassrooms(tempUser.getClassrooms());
			user.setRole(tempUser.getRole());
			return ResponseEntity.ok(userRepository.save(user));
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public User deleteUser(long id) {
		return userRepository.deleteById(id);
	}

	public ResponseEntity<Object> getClassroom(String token, long classroomId) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				if (user.getClassrooms().containsKey(classroomId)) {
					return ResponseEntity.ok(user.getClassrooms().get(classroomId));
				}
			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classroomId);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> getClassrooms(String token) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				List<Classroom> classroomList=new ArrayList<Classroom>(user.getClassrooms().values());
				return ResponseEntity.ok(classroomList);
			}
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> createClassroom(String token, String name) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			Classroom classRoom = new Classroom();
			if (name.trim() != "" && name != null) {
				classRoom.setName(name);
				classRoom.setOwner(user.getEmail());
				classRoom.setDateCreated(LocalDateTime.now());
				classRoom.setTasks(new Hashtable<Long, Task>());
				Classroom classroomToSend = classroomRepository.save(classRoom);
				user.getClassrooms().put(classroomToSend.getId(), classRoom);
				userRepository.save(user);
				return ResponseEntity.ok(classroomToSend);
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Classroom name cant be null/empty");
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> updateClassroom(String token, Classroom classRoom) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				Classroom tempClassroom = user.getClassrooms().get(classRoom.getId());
				if (tempClassroom != null) {
					if (tempClassroom.getOwner().equalsIgnoreCase(user.getEmail())) {
						classRoom.setTasks(tempClassroom.getTasks());
						return ResponseEntity.ok(classroomRepository.save(classRoom));
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BadRequestsResponse.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classRoom.getId());
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);

	}

	public ResponseEntity<Object> deleteClassroom(String token, long classroomId) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				Classroom classRoom = user.getClassrooms().get(classroomId);
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
						user.getClassrooms().remove(classroomId);
						classroomRepository.delete(classRoom);
						userRepository.save(user);
						return ResponseEntity.ok(classroomId);
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BadRequestsResponse.OWNER);
				}

			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classroomId);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> addUserToClassroom(String token, String userEmail, long classRoomId) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		User userToAdd = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			Classroom classRoom = classroomRepository.getOne(classRoomId);
			if (classRoom != null) {
				if (classRoom.getOwner().equalsIgnoreCase(user.getEmail())) {
					userToAdd = userRepository.findByEmail(userEmail);
					List<Long> userList = userRepository.findUserInClassroom(classRoomId);
					if(!userList.contains(userToAdd.getId())) {
						if (userToAdd.getClassrooms() != null) {
							userToAdd.getClassrooms().put(classRoomId, classRoom);
							userRepository.save(userToAdd);
							return ResponseEntity.ok(userToAdd.getId());
						}
					}
					return ResponseEntity.badRequest().body("User already in classroom :(");
			
			}
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BadRequestsResponse.OWNER);
		}

			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classRoomId);
		
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> leaveClassroom(String token, long classRoomId) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			Classroom classRoom = classroomRepository.getOne(classRoomId);
			if (classRoom != null) {
				if (user.getClassrooms().containsKey(classRoomId)) {
					user.getClassrooms().remove(classRoomId);
					return ResponseEntity.ok(userRepository.save(user));
				}
				return ResponseEntity.badRequest().body("You are not a part of this classroom");
			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classRoomId);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> removeUser(String token, long userId, long classRoomId) {
		ResponseEntity<?> response = findCurrentUser(token);
		User currentUser = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			currentUser = (User) response.getBody();
			User user = userRepository.getOne(userId);
			Classroom classRoom = classroomRepository.getOne(classRoomId);
			if (user != null) {
				if (classRoom != null) {
					if (classRoom.getOwner().equalsIgnoreCase(currentUser.getEmail())) {
						if (user.getClassrooms().containsKey(classRoomId)) {
							user.getClassrooms().remove(classRoomId);
							String name = userRepository.save(user).getFullName();
							return ResponseEntity.ok(name + " has been removed from the classroom");
						}
						return ResponseEntity.badRequest()
								.body("User with id " + userId + " is not a part of classroom with id " + classRoomId);

					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BadRequestsResponse.OWNER);
				}
				return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classRoomId);
			}

		}

		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

	public ResponseEntity<Object> transferClassroom(String token, long classroomId, String email) {
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				Classroom tempClassroom = user.getClassrooms().get(classroomId);
				if (tempClassroom != null) {
					if (tempClassroom.getOwner().equalsIgnoreCase(user.getEmail())) {
						List<Long> userList = userRepository.findUserInClassroom(classroomId);
						User newOwner = userRepository.findByEmail(email);
						if (newOwner != null) {
							if (userList.contains(newOwner.getId())) {
								tempClassroom.setOwner(newOwner.getEmail());
								ResponseEntity.ok(classroomRepository.save(tempClassroom));
							}
							return ResponseEntity.badRequest().body("The user is not a part of this classroom");
						}
						return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BadRequestsResponse.OWNER);
				}
			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classroomId);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);

	}

	public ResponseEntity<Object> getUsersInclassroom(String token, long classroomId) {
		List<Long> usersidzList = null;
		List<User> usersList = new ArrayList<User>();
		ResponseEntity<?> response = findCurrentUser(token);
		User user = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			user = (User) response.getBody();
			if (user.getClassrooms() != null && user.getClassrooms().size() > 0) {
				if (user.getClassrooms().containsKey(classroomId)) {
					usersidzList = userRepository.findUserInClassroom(classroomId);
					for (long userId : usersidzList) {
						Optional<User> tempUser = userRepository.findById(userId);
						if (tempUser.isPresent()) {
							usersList.add(tempUser.get());
						}
					}
					return ResponseEntity.ok(usersList);
				}
			}
			return ResponseEntity.badRequest().body(BadRequestsResponse.CLASSROOM_ID_NOT_FOUND + classroomId);
		}
		return ResponseEntity.badRequest().body(BadRequestsResponse.USER_NOT_FOUND);
	}

}
