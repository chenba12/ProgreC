package com.progresee.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.progresee.app.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;
	
	//User Stuff
	
	// http://localhost:5000/admin/getUser/email
	@GetMapping("/getUser")
	public ResponseEntity<Object> getUser(@RequestParam String email) {
		return ResponseEntity.ok(userService.getUser(email));
	}

	// http://localhost:5000/admin/getAll
	@GetMapping("/getAll")
	public ResponseEntity<Object> getAllUsers() {
		return userService.getUsers();
	}

	// http://localhost:5000/admin/deleteUser
	@DeleteMapping("/deleteUser")
	public ResponseEntity<Object> deleteUser(@RequestParam long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok("User with id " + id + "has been deleted");
	}
	
}
