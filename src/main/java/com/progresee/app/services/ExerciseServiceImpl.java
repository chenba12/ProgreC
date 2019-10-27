package com.progresee.app.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.progresee.app.beans.Exercise;
import com.progresee.app.services.dao.ExerciseService;
import com.progresee.app.utils.ResponseUtils;

@Service
public class ExerciseServiceImpl implements ExerciseService {

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
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		DocumentReference docRef = firestore.collection("exercises").document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				return documentSnapshot.getData();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getExercise");
	}

	@Override
	public Map<String, Object> getFinishedUsers(String token, String classroomId, String taskId, String exerciseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAllExercises(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		Query docRef = firestore.collection("exercises").whereEqualTo("taskUid", taskId);
		try {
			ApiFuture<QuerySnapshot> documentReference = docRef.get();
			QuerySnapshot documentSnapshot = documentReference.get();
			List<Exercise> tempExercises = documentSnapshot.toObjects(Exercise.class);
			Map<String, Object> exercises = new Hashtable<>();
			for (Exercise exercise : tempExercises) {
				exercises.put(exercise.getUid(), exercise);
			}
			return exercises;

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getAllExercises");
	}

	@Override
	public Map<String, Object> createExercise(String token, String classroomId, String taskId, Exercise exercise) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			exercise.setDateCreated(Calendar.getInstance().getTime());
			exercise.setTaskUid(taskId);
			exercise.setUsrsFinishedList(new ArrayList<String>());
			String exerciseUid = UUID.randomUUID().toString().replace("-", "");
			exercise.setUid(exerciseUid);
			ApiFuture<WriteResult> docRef = firestore.collection("exercises").document(exerciseUid).set(exercise);
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return getExerciseAfterRequest(exerciseUid);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/createExercise");
	}

	@Override
	public Map<String, Object> deleteExercise(String token, String classroomId, String taskId, String exerciseId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
			ApiFuture<WriteResult> docRef = firestore.collection("exercises").document(exerciseId).delete();
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return ResponseUtils.generateSuccessString("Exercise has been deleted");
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/deleteExercise");
	}

	@Override
	public Map<String, Object> updateExercise(String token, String classroomId, String taskId, Exercise exercise) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
			ApiFuture<WriteResult> docRef = firestore.collection("exercises").document(exercise.getUid()).set(exercise);
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return getExerciseAfterRequest(exercise.getUid());
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/updateExercise");
	}

	@Override
	public Map<String, Object> updateStatus(String token, String classroomId, String taskId, String exerciseId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
			ApiFuture<WriteResult> docRef = firestore.collection("tasks").document(exerciseId).update("usrsFinishedList",FieldValue.arrayUnion(uid));
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return getExerciseAfterRequest(exerciseId);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/updateExercise");
	}

	private Map<String, Object> getExerciseAfterRequest(String exerciseUid) {
		DocumentReference docRef = firestore.collection("exercises").document(exerciseUid);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				return documentSnapshot.getData();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.ERROR, "");
	}

}
