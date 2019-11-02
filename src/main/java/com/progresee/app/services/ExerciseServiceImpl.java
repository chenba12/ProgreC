package com.progresee.app.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.progresee.app.beans.Exercise;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.UserFinished;
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
	public Map<String, Object> getFinishedUsers(String token, String classroomId, String exerciseId) {
		Map<String, String> usersInClassroom = new Hashtable<String, String>();
		Map<String, Object> usersFinishedList = new Hashtable<String, Object>();
		Map<String, Object> finishedUsers = new Hashtable<String, Object>();
		usersInClassroom = userService.getUsersInClassroomNoToken(classroomId);
		System.out.println("usersinclassroom------>" + usersInClassroom);
		DocumentReference docRef = firestore.collection("exercises").document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				usersFinishedList = (Map<String, Object>) documentSnapshot.get("usersFinishedList");
				System.out.println("usersFinishedList------------->" + usersFinishedList);
				UserFinished userFinished = new UserFinished();
				String userFinishedUid = UUID.randomUUID().toString().replace("-", "");
				userFinished.setUid(userFinishedUid);
				userFinished.setExerciseUid(exerciseId);
				for (String it : usersInClassroom.keySet()) {
					if (usersFinishedList.containsKey(it)) {
						userFinished.setTimestamp(Calendar.getInstance().getTime().toString());
						userFinished.setHasFinished("1");
						userFinished.setEmail(usersInClassroom.get(it));
						finishedUsers.put(usersInClassroom.get(it), userFinished);

					} else {
						userFinished.setTimestamp("N/A");
						userFinished.setHasFinished("0");
						userFinished.setEmail(usersInClassroom.get(it));
						finishedUsers.put(usersInClassroom.get(it), userFinished);

					}
				}
				System.out.println("finishedUsers------------->" + finishedUsers);
				return finishedUsers;
			}
		} catch (Exception e) {
		}
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
	public Map<String, Object> createExercise(String token, String classroomId, String taskId, String description) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			Exercise exercise = new Exercise();
			exercise.setDateCreated(Calendar.getInstance().getTime());
			exercise.setTaskUid(taskId);
			exercise.setUsersFinishedList(new Hashtable<String, Object>());
			String exerciseUid = UUID.randomUUID().toString().replace("-", "");
			exercise.setUid(exerciseUid);
			exercise.setExerciseTitle(description);
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
	public Map<String, Object> updateStatus(String token, String classroomId, String exerciseId,
			String hasFinished) {
		System.out.println(hasFinished);
		Map<String, Object> map = userService.findCurrentUser(token);
		Map<String, Object> usersFinishedList = new Hashtable<String, Object>();
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		try {
			if (hasFinished.equals("1")) {
				Map<String, Object> uidAndDate = new Hashtable<String, Object>();
				uidAndDate.put(uid, Calendar.getInstance().getTime().toString());
				usersFinishedList.put("usersFinishedList", uidAndDate);
				ApiFuture<WriteResult> writeNewlyAdded = firestore.collection("exercises").document(exerciseId)
						.set(usersFinishedList, SetOptions.merge());

				WriteResult writeResult = writeNewlyAdded.get();
				if (writeResult != null) {
					return getExerciseAfterRequest(exerciseId);
				}
			} else if (hasFinished.equals("0")) {
				
			
				DocumentReference docRef = firestore.collection("exercises").document(exerciseId);
				ApiFuture<DocumentSnapshot> documentReference = docRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					Map<String, Object> existingMap = (Map<String, Object>) documentSnapshot.get("usersFinishedList");
					if (existingMap.containsKey(uid)) {
						existingMap.remove(uid);
						usersFinishedList.put("usersFinishedList", existingMap);
						ApiFuture<WriteResult> writeExisting = firestore.collection("exercises").document(exerciseId)
								.set(usersFinishedList, SetOptions.merge());

						WriteResult writeResult = writeExisting.get();
						if (writeResult != null) {
							return getExerciseAfterRequest(exerciseId);
						}
					}
				}
		
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/updateExercise");
	}

	private Map<String, Object> getExerciseAfterRequest(String exerciseId) {
		DocumentReference docRef = firestore.collection("exercises").document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Exercise exercise = documentSnapshot.toObject(Exercise.class);
			Map<String, Object> map = new Hashtable<>();
			map.put(exerciseId, exercise);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
