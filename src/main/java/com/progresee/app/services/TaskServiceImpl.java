package com.progresee.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.progresee.app.beans.Task;
import com.progresee.app.services.dao.TaskService;
import com.progresee.app.utils.ResponseUtils;
import java.util.Calendar;
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

	@Autowired
	private UserServiceImpl userService;

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
		Query docRef = firestore.collection("classrooms").document(classroomId).collection("tasks");
		try {
			ApiFuture<QuerySnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			QuerySnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			List<Task> tempTasks = documentSnapshot.toObjects(Task.class);
			System.out.println("classrooms IN LIST---->" + tempTasks);
			Map<String, Object> tasks = new Hashtable<>();
			for (Task task : tempTasks) {
				tasks.put(task.getUid(), task);
			}
			return tasks;

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public Map<String, Object> getTask(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		DocumentReference docRef = firestore.collection("classrooms").document(classroomId).collection("tasks")
				.document(taskId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			if (documentSnapshot.exists()) {
				return documentSnapshot.getData();
			}
			response.setStatus(400);
			return ResponseUtils.generateErrorCode(400, "you are not part of this classroom", "/getTask");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> createTask(String token, String classroomId, Task task) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		task.setTitle(task.getTitle());
		task.setStartDate(Calendar.getInstance().getTime());
		String taskUid = UUID.randomUUID().toString().replace("-", "");
		task.setUid(taskUid);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).collection("tasks")
				.document(taskUid).set(task);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult.getUpdateTime() != null) {
				return getTaskAfterRequest(classroomId, taskUid);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> deleteTask(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).collection("tasks")
				.document(taskId).delete();
		try {
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return ResponseUtils.generateSuccessString("Task has been deleted");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> updateTask(String token, String classroomId, Task task) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).collection("tasks")
				.document(task.getUid()).set(task);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult.getUpdateTime() != null) {
				return getTaskAfterRequest(classroomId, task.getUid());
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Todo finish this with android
	@Override
	public Map<String, Object> updateTaskImage(String token, String classroomId, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	private Map<String, Object> getTaskAfterRequest(String classroomId, String taskId) {
		DocumentReference docRef = firestore.collection("classrooms").document(classroomId).collection("tasks")
				.document(taskId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			Task task = documentSnapshot.toObject(Task.class);
			Map<String, Object> map = new Hashtable<>();
			map.put(taskId, task);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}