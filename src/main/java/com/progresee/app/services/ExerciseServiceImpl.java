package com.progresee.app.services;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.progresee.app.beans.Exercise;
import com.progresee.app.services.dao.ExerciseService;


@Service
public class ExerciseServiceImpl implements ExerciseService{

	

	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	Firestore firestore;

	@Autowired
	HttpServletResponse response;

	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> ExerciseService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> ExerciseService");
	}

	@Override
	public Map<String, Object> getExercise(String token, String classroomId, String taskId, String exerciseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getFinishedUsers(String token, String classroomId, String taskId, String exerciseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAllExercises(String token, String classroomId, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> createExercise(String token, String classroomId, String taskId, Exercise exercise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> deleteExercise(String token, String classroomId, String taskId, String exerciseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateExercise(String token, String classroomId, String taskId, Exercise exercise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateStatus(String token, String classroomId, String taskId, String exerciseId) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
