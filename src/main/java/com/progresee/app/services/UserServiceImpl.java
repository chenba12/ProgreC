package com.progresee.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Role;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.User;
import com.progresee.app.repositories.ClassroomRepository;
import com.progresee.app.repositories.UserRepository;
import com.progresee.app.services.dao.UserService;
import com.progresee.app.utils.BadRequestsResponse;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	Firestore firestore;
	
	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> UserService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> UserService");
	}

	@Override
	public Map<String, Object> getUser(String token) {
		
		return null;
	}

	@Override
	public Map<String, Object> updateUser(String token, org.apache.catalina.User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getClassroom(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getClassrooms(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> createClassroom(String token, String classroomName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateClassroom(String token, String classroomName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> deleteClassroom(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getUsersInClassroom(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> transferClassroom(String token, String newOwnerId, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> addToClassroom(String token, String userEmail, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> leaveClassroom(String token, String classroomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> removeFromClassroom(String token, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> findCurrentUser(String token) {
		FirebaseToken decodedToken;
		try {
			decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			String uid = decodedToken.getUid();
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}
		 
		
	}


}
