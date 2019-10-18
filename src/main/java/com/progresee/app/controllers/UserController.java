package com.progresee.app.controllers;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.v1beta1.Document;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.User;
import com.progresee.app.services.UserService;
import com.progresee.app.utils.BadRequestsResponse;
import com.progresee.app.utils.ErrorUtils;
import com.progresee.app.utils.NullCheckerUtils;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	private UserServiceImpl service;

	@Autowired
	HttpServletResponse res;

	@GetMapping("/firebaseUsers")
	public Map<String, Object> getFirebaseUser() throws InterruptedException, ExecutionException {

		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("users").document("asfasdfa");
		// asynchronously retrieve the document
		ApiFuture<DocumentSnapshot> future = docRef.get();
		// ...
		// future.get() blocks on response
		DocumentSnapshot document = future.get();
		if (document.exists()) {
		  return document.getData();
		} else {
		 res.setStatus(400);
		 return ErrorUtils.generateErrorCode(400, "asdgfaskjdhf", "/firebaseUsers");
		}


	}

	@GetMapping("/firebaseUserWrite")
	public ResponseEntity<Object> writeFirebaseUser() throws InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();
		fbUser fbUser=new fbUser();
		fbUser.setEmail("email");
		fbUser.setName("chen");
		fbUser.setTimestampFieldValue(FieldValue.serverTimestamp());

		ApiFuture<DocumentReference> docRef = db.collection("Users").add(fbUser);
		// asynchronously retrieve the document
		DocumentReference future = docRef.get();
		// ...
		// future.get() blocks on response
		ApiFuture<DocumentSnapshot> document = future.get();

		  System.out.println("Document data: " + document.get().getData());

		return null;

	}

	@GetMapping("/firebasetask")
	public ResponseEntity<Object> firebasetask() throws InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();


		CollectionReference docRef = db.collection("Classrooms").document("NwW2VsENepvjQYdsGceO").collection("Tasks");
		// asynchronously retrieve the document
		ApiFuture<QuerySnapshot> future = docRef.get();
		// ...
		// future.get() blocks on response
		QuerySnapshot document = future.get();

		  System.out.println("Document data: " + document.getDocuments().size());

		return null;

	}





	@GetMapping("/getCurrentUser")
	public ResponseEntity<Object> getCurrentUser(@RequestHeader("Authorization") String token) {
		return service.getCurrentUser(token);
	}

	// http://localhost:5000/user/updateUser
	@PutMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
		if (NullCheckerUtils.userNullChecker(user)) {
			return service.updateUser(token, user);
		}
		return ResponseEntity.badRequest().body("User values cannot be null/empty");
	}

	@GetMapping("/getClassroom")
	public ResponseEntity<Object> getClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long classroomId) {
		return service.getClassroom(token, classroomId);
	}

	@GetMapping("/getClassrooms")
	public ResponseEntity<Object> getClassrooms(@RequestHeader("Authorization") String token) {
		return service.getClassrooms(token);
	}


	// http://localhost:5000/user/createClassroom
	@PostMapping("/createClassroom")
	public ResponseEntity<Object> createClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String name) {
		return service.createClassroom(token, name);
	}

	@PutMapping("/updateClassroom")
	public ResponseEntity<Object> updateClassroom(@RequestHeader("Authorization") String token,
			@RequestBody Classroom classroom) {
		if (NullCheckerUtils.classroomNullChecker(classroom)) {
			return service.updateClassroom(token, classroom);
		}
		return ResponseEntity.badRequest().body("Classroom values cannot be null/empty");
	}

	@DeleteMapping("/deleteClassroom")
	public ResponseEntity<Object> deleteClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long classroomId) {
		return service.deleteClassroom(token,classroomId);
	}

	@GetMapping("/getUsersInClassroom")
	public ResponseEntity<Object> getUsersInClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long classroomId) {
		return service.getUsersInclassroom(token,classroomId);
	}

	@PutMapping("/transferClassroom")
	public ResponseEntity<Object> transferClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long classroomId, @RequestParam String email) {
		return service.transferClassroom(token, classroomId, email);
	}

	// http://localhost:5000/user/addToClassroom
	@PutMapping("addToClassroom")
	public ResponseEntity<Object> addToClassroom(@RequestHeader("Authorization") String token,
			@RequestParam String userEmail, @RequestParam long classroomId) {
		return service.addUserToClassroom(token, userEmail, classroomId);
	}

	// http://localhost:5000/user/leaveClassroom
	@PutMapping("leaveClassroom")
	public ResponseEntity<Object> leaveClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long classroomId) {
		return service.leaveClassroom(token, classroomId);
	}

	// http://localhost:5000/user/removeUser
	@PutMapping("removeUser")
	public ResponseEntity<Object> removeFromClassroom(@RequestHeader("Authorization") String token,
			@RequestParam long userId, @RequestParam long classroomId) {
		return service.removeUser(token, userId, classroomId);
	}


}
