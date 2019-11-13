package com.progresee.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Exercise;
import com.progresee.app.beans.Task;

@Component
public class SchedulingTaskService {
	private static final String EXERCISES = "exercises";
	private static final String CLASSROOMS = "classrooms";
	private static final String TASKS = "tasks";

	@Autowired
	private Firestore firestore;
	
	
	@Scheduled(fixedRate = 5000000)
	public void deleteArchivedBeans() {
		
		try {
			Query classroomRef = firestore.collection(CLASSROOMS).whereEqualTo("archived", true);
			ApiFuture<QuerySnapshot> classroomsReference = classroomRef.get();
			QuerySnapshot classroomsSnapshot = classroomsReference.get();
			List<Classroom> tempClassrooms = classroomsSnapshot.toObjects(Classroom.class);
		if (tempClassrooms!=null && tempClassrooms.size()>0) {
			for (Classroom classroom : tempClassrooms) {
				System.out.println(classroom+" is about to be deleted.....");
				ApiFuture<WriteResult> deleteClassromRef = firestore.collection(CLASSROOMS).document(classroom.getUid()).delete();
				deleteClassromRef.get();	
			}
		}
		Query taskRef = firestore.collection(TASKS).whereEqualTo("archived", true);
			ApiFuture<QuerySnapshot> tasksReference = taskRef.get();
			QuerySnapshot tasksSnapshot = tasksReference.get();
			List<Task> tempTasks = tasksSnapshot.toObjects(Task.class);
		if (tempTasks!=null && tempTasks.size()>0) {
			for (Task task : tempTasks) {
				System.out.println(task+" is about to be deleted.....");
				ApiFuture<WriteResult> deleteTaskRef = firestore.collection(TASKS).document(task.getUid()).delete();
				deleteTaskRef.get();	
			}
		}
		Query exerciseRef = firestore.collection(EXERCISES).whereEqualTo("archived", true);
		ApiFuture<QuerySnapshot> exercisesReference = exerciseRef.get();
		QuerySnapshot exercisesSnapshot = exercisesReference.get();
		List<Exercise> tempExercises = exercisesSnapshot.toObjects(Exercise.class);
	if (tempExercises!=null && tempExercises.size()>0) {
		for (Exercise exercise : tempExercises) {
			System.out.println(exercise+" is about to be deleted.....");
			ApiFuture<WriteResult> deleteExercisesRef = firestore.collection(EXERCISES).document(exercise.getUid()).delete();
			deleteExercisesRef.get();	
		}
	}
	System.out.println("Finished Scheduling Task ..... ;D");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
