package com.progresee.app.controllers;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import com.progresee.app.beans.User;
import com.progresee.app.services.UserServiceImpl;
import com.progresee.app.utils.NullCheckerUtils;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	private UserServiceImpl service;

	@Autowired
	HttpServletResponse res;

	@GetMapping("/getCurrentUser")
	public ResponseEntity<Object> getCurrentUser(@RequestHeader("Authorization") String token) {
		return service.getUser(token);
	}

	// http://localhost:5000/user/updateUser
	@PutMapping("/updateUser")
	public Map<String, Object> updateUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
		if (NullCheckerUtils.userNullChecker(user)) {
			return service.updateUser(token, user);
		}
		return null;
	}

	@GetMapping("/getClassroom")
	public Map<String, Object> getClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId) {
		return service.getClassroom(token, classroomId);
	}

	@GetMapping("/getClassrooms")
	public Map<String, Object> getClassrooms(@RequestHeader("Authorization") String token) {
		return service.getClassrooms(token);
	}

	// http://localhost:5000/user/createClassroom
	@PostMapping("/createClassroom")
	public Map<String, Object> createClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String name,@RequestParam String description) {
		return service.createClassroom(token, name,description);
	}

	@PutMapping("/updateClassroom")
	public Map<String, Object> updateClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String name,@RequestParam String description) {
		return service.updateClassroom(token, classroomId, name,description);

	}

	@DeleteMapping("/deleteClassroom")
	public Map<String, Object> deleteClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId) {
		return service.deleteClassroom(token, classroomId);
	}

	@GetMapping("/getUsersInClassroom")
	public Map<String, Object> getUsersInClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId) {
		return service.getUsersInClassroom(token, classroomId);
	}

	@PutMapping("/transferClassroom")
	public Map<String, Object> transferClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String newOwnerId) {
		return service.transferClassroom(token, classroomId, newOwnerId);
	}

	// http://localhost:5000/user/addToClassroom
	@PutMapping("addToClassroom")
	public Map<String, Object> addToClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String email) {
		return service.addToClassroom(token, classroomId, email);
	}

	// http://localhost:5000/user/leaveClassroom
	@PutMapping("leaveClassroom")
	public Map<String, Object> leaveClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId) {
		return service.leaveClassroom(token, classroomId);
	}

	// http://localhost:5000/user/removeUser
	@PutMapping("removeUser")
	public Map<String, Object> removeFromClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String classroomId, @RequestParam String userId) {
		return service.removeFromClassroom(token, classroomId, userId);
	}

}
