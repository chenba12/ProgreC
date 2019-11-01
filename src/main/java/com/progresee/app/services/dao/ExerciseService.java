package com.progresee.app.services.dao;

import java.util.Map;

import com.progresee.app.beans.Exercise;

public interface ExerciseService {

	public Map<String, Object> getExercise(String token, String classroomId, String taskId, String exerciseId);

	public Map<String, Object> getFinishedUsers(String token, String classroomId, String taskId, String exerciseId);

	public Map<String, Object> getAllExercises(String token, String classroomId, String taskId);

	public Map<String, Object> createExercise(String token, String classroomId, String taskId, String description);

	public Map<String, Object> deleteExercise(String token, String classroomId, String taskId, String exerciseId);

	public Map<String, Object> updateExercise(String token, String classroomId, String taskId, Exercise exercise);

	public Map<String, Object> updateStatus(String token, String classroomId, String taskId, String exerciseId);


}
