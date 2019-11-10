package com.progresee.app.services;

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
import com.progresee.app.beans.Task;
import com.progresee.app.services.dao.TaskService;
import com.progresee.app.utils.DateUtils;
import com.progresee.app.utils.ResponseUtils;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

@Service
public class TaskServiceImpl implements TaskService {

	private static final String TASKS = "tasks";
	private static final String EXERCISES = "exercises";

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private ExerciseServiceImpl exerciseService;

	@Autowired
	Firestore firestore;

	@Autowired
	HttpServletResponse response;

	@PostConstruct
	public void initDB() {
		System.out.println("Postconstruct -----> TaskSerivce");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> TaskService");
	}

	@Override
	public Map<String, Object> getAllTasks(String token, String classroomId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		try {
			if (userService.checkIfUserIsPartOfClassroom(classroomId, uid)) {
				Query docRef = firestore.collection(TASKS).whereEqualTo("classroomUid", classroomId);
				ApiFuture<QuerySnapshot> documentReference = docRef.get();
				QuerySnapshot documentSnapshot = documentReference.get();
				List<Task> tempTasks = documentSnapshot.toObjects(Task.class);
				Map<String, Object> tasks = new Hashtable<>();
				for (Task task : tempTasks) {
					tasks.put(task.getUid(), task);
				}
				if (!tasks.isEmpty()) {
					return tasks;
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getAllTasks");
	}

	@Override
	public Map<String, Object> getTask(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkIfUserIsPartOfClassroom(classroomId, uid)) {
			DocumentReference docRef = firestore.collection(TASKS).document(taskId);
			try {
				ApiFuture<DocumentSnapshot> documentReference = docRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					return documentSnapshot.getData();
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getAllTasks");
	}

	@Override
	public Map<String, Object> createTask(String token, String classroomId, String title, String description,
			String link, String endDate) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			try {
				Task task = new Task();
				task.setStartDate(DateUtils.formatDate());
				String taskUid = UUID.randomUUID().toString().replace("-", "");
				task.setUid(taskUid);
				task.setArchived(false);
				task.setClassroomUid(classroomId);
				task.setCompleted(false);
				task.setTitle(title);
				task.setDescription(description);
				if (link != null) {
					task.setReferenceLink(link);
				}
				task.setEndDate(endDate);
				ApiFuture<WriteResult> docRef = firestore.collection(TASKS).document(taskUid).set(task);

				WriteResult addTaskResult = docRef.get();
				if (addTaskResult != null) {

					DocumentReference getRef = firestore.collection(TASKS).document(taskUid);
					ApiFuture<DocumentSnapshot> documentReference = getRef.get();
					DocumentSnapshot documentSnapshot = documentReference.get();
					System.out.println("documentSnapshot :(");
					if (documentSnapshot.exists()) {
						System.out.println("documentSnapshot EXISTS");
						ApiFuture<WriteResult> addTaskToClassroom = firestore.collection("classrooms")
								.document(classroomId).update("numberOfTasks", FieldValue.increment(1));
						WriteResult addTaskToClassroomResult = addTaskToClassroom.get();
						if (addTaskToClassroomResult != null) {
							return getTaskAfterRequest(classroomId, taskUid);
						}
					}
				}

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/createTask");
	}

	@Override
	public Map<String, Object> deleteTask(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection(TASKS).document(taskId).update("archived", true);
			try {
				DocumentReference getRef = firestore.collection(TASKS).document(taskId);
				ApiFuture<DocumentSnapshot> documentReference = getRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					ApiFuture<WriteResult> removeTask = firestore.collection("classrooms").document(classroomId)
							.update("numberOfTasks", FieldValue.increment(-1));
					removeTask.get();
					Query archiveRef = firestore.collection(EXERCISES).whereEqualTo("taskUid", taskId);
					ApiFuture<QuerySnapshot> archivedReference = archiveRef.get();
					QuerySnapshot archivedSnapshot = archivedReference.get();
					List<Exercise> tempExercises = archivedSnapshot.toObjects(Exercise.class);
					Map<String, Object> exercises = new Hashtable<>();
					System.out.println("exercises list doomed for archiving --> " + exercises);
					for (Exercise exercise : tempExercises) {
						System.out.println("exercise with uid " + exercise.getUid() + " archived");
						exerciseService.archiveExercisesFromTask(exercise.getUid());
					}
					return ResponseUtils.generateSuccessString("Task has been archived");
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/deleteTask");
	}
	
	public Map<String, Object> archiveTaskFromClassrrom(String classroomId, String taskId) {
			ApiFuture<WriteResult> docRef = firestore.collection(TASKS).document(taskId).update("archived", true);
			try {
				DocumentReference getRef = firestore.collection(TASKS).document(taskId);
				ApiFuture<DocumentSnapshot> documentReference = getRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					ApiFuture<WriteResult> removeTask = firestore.collection("classrooms").document(classroomId)
							.update("numberOfTasks", FieldValue.increment(-1));
					removeTask.get();
					Query archiveRef = firestore.collection(EXERCISES).whereEqualTo("taskUid", taskId);
					ApiFuture<QuerySnapshot> archivedReference = archiveRef.get();
					QuerySnapshot archivedSnapshot = archivedReference.get();
					List<Exercise> tempExercises = archivedSnapshot.toObjects(Exercise.class);
					Map<String, Object> exercises = new Hashtable<>();
					System.out.println("exercises list doomed for archiving --> " + exercises);
					for (Exercise exercise : tempExercises) {
						System.out.println("exercise with uid " + exercise.getUid() + " archived");
						exerciseService.archiveExercisesFromTask(exercise.getUid());
					}
					return ResponseUtils.generateSuccessString("Task has been archived");
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/deleteTask");
	}


	@Override
	public Map<String, Object> updateTask(String token, String classroomId, Task task) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection(TASKS).document(task.getUid()).set(task);
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return getTaskAfterRequest(classroomId, task.getUid());
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/updateTask");

	}

	private Map<String, Object> getTaskAfterRequest(String classroomId, String taskId) {
		DocumentReference docRef = firestore.collection(TASKS).document(taskId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Task task = documentSnapshot.toObject(Task.class);
			Map<String, Object> map = new HashMap<>();
			map.put(taskId, task);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}