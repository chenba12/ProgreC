package com.progresee.app.services.dao;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.progresee.app.beans.Exercise;

public interface ExerciseService {

	public ResponseEntity<Object> getExercise(String token, String classroomId, String taskId, String exerciseId);

	public Map<String, Object> getAllExercises(String token, String classroomId, String taskId);

	public ResponseEntity<Object> createExercise(String token, String classroomId, String taskId, String description);

	public ResponseEntity<Object> deleteExercise(String token, String classroomId, String taskId, String exerciseId);

	public ResponseEntity<Object> updateExercise(String token, String classroomId, String taskId, Exercise exercise);

	public Map<String, Object> getFinishedUsers(String token, String classroomId, String exerciseId);

	public ResponseEntity<Object> updateStatus(String token, String classroomId, String exerciseId);


}
