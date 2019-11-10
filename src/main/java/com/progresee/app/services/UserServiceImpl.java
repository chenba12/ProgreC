package com.progresee.app.services;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.Exercise;
import com.progresee.app.beans.Task;
import com.progresee.app.beans.User;
import com.progresee.app.services.dao.UserService;
import com.progresee.app.utils.DateUtils;
import com.progresee.app.utils.ResponseUtils;

@Service
public class UserServiceImpl implements UserService {

	private static final String USERS = "users";
	private static final String CLASSROOMS = "classrooms";
	private static final String TASKS = "tasks";

	@Autowired
	private Firestore firestore;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private TaskServiceImpl taskServiceImpl;

	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> UserService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> UserService");
	}

	@Override
	public ResponseEntity<Object> getUser(String token) {

		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			String uid = decodedToken.getUid();
			Map<String, Object> map = new HashMap<>();
			DocumentReference documentReference = firestore.collection(USERS).document(uid);
			ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
			DocumentSnapshot documentSnapshot = apiFuture.get();
			if (documentSnapshot.exists()) {
				map.put("signedIn", DateUtils.formatDate());
				ApiFuture<WriteResult> updatedDocumentReference = firestore.collection(USERS).document(uid).update(map);
				if (updatedDocumentReference != null) {
					return ResponseEntity.ok(documentSnapshot.toObject(User.class));
				}
			} else {
				User user = new User();
				user.setDateCreated(DateUtils.formatDate());
				user.setSignedIn(DateUtils.formatDate());
				user.setFullName(decodedToken.getName());
				user.setEmail(decodedToken.getEmail());
				user.setProfilePictureUrl(decodedToken.getPicture());
				user.setUid(uid);
				ApiFuture<WriteResult> docRef = firestore.collection(USERS).document(uid).set(user);
				if (docRef != null) {
					return ResponseEntity.ok(user);
				}
			}
		} catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> findCurrentUser(String token) {
		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			String uid = decodedToken.getUid();
			Map<String, Object> map = new HashMap<>();
			DocumentReference documentReference = firestore.collection(USERS).document(uid);
			ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
			DocumentSnapshot documentSnapshot = apiFuture.get();
			map.put("signedIn", DateUtils.formatDate());
			if (documentSnapshot.exists()) {
				ApiFuture<WriteResult> updatedDocumentReference = firestore.collection(USERS).document(uid).update(map);
				if (updatedDocumentReference != null) {
					map.clear();
					map.put("uid", uid);
					map.put("email", documentSnapshot.getData().get("email"));
					return map;
				}
			} else {
				User user = new User();
				user.setDateCreated(DateUtils.formatDate());
				user.setSignedIn(DateUtils.formatDate());
				user.setFullName(decodedToken.getName());
				user.setEmail(decodedToken.getEmail());
				user.setProfilePictureUrl(decodedToken.getPicture());
				user.setUid(uid);
				ApiFuture<WriteResult> docRef = firestore.collection(USERS).document(uid).set(user);
				if (docRef != null) {
					System.out.println("user added " + user);
					map.clear();
					map.put("uid", uid);
					map.put("email", decodedToken.getEmail());
					return map;
				}
			}
		} catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> updateUser(String token, User user) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		ApiFuture<WriteResult> docRef = firestore.collection(USERS).document(uid).set(user);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult != null) {
				return getUserAfterRequest(uid);
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		try {
			DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Map<String, String> users = (Map<String, String>) documentSnapshot.get("userList");
			if (users.containsKey(uid)) {
				return documentSnapshot.getData();
			}
			response.setStatus(ResponseUtils.BAD_REQUEST);
			return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
					"/getClassroom");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getClassrooms(String token) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		Query docRef = firestore.collection(CLASSROOMS);
		try {
			ApiFuture<QuerySnapshot> documentReference = docRef.get();
			QuerySnapshot documentSnapshot = documentReference.get();
			List<Classroom> tempClassrooms = documentSnapshot.toObjects(Classroom.class);
			Map<String, Object> classrooms = new HashMap<>();
			for (Classroom classroom : tempClassrooms) {
				if (classroom.getUserList().containsKey(uid)) {
					classrooms.put(classroom.getUid(), classroom);
				}

			}
			if (!classrooms.isEmpty()) {
				return classrooms;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getClassrooms");

	}

	@Override
	public Map<String, Object> createClassroom(String token, String classroomName, String description) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		System.out.println(description);
		String uid = (String) map.get("uid");
		String owner = (String) map.get("email");
		Classroom classroom = new Classroom();
		classroom.setName(classroomName);
		classroom.setDescription(description);
		classroom.setNumberOfTasks(0);
		classroom.setOwner(owner);
		classroom.setArchived(false);
		classroom.setNumberOfUsers(1);
		classroom.setDateCreated(DateUtils.formatDate());
		classroom.getUserList().put(uid, owner);
		classroom.setOwnerUid(uid);
		String classroomUid = UUID.randomUUID().toString().replace("-", "");
		classroom.setUid(classroomUid);
		ApiFuture<WriteResult> docRef = firestore.collection(CLASSROOMS).document(classroomUid).set(classroom);
		try {
			WriteResult documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			return getClassroomAfterRequest(classroomUid);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, "Failed to create classroom",
				"/createClassroom");
	}

	@Override
	public Map<String, Object> updateClassroom(String token, String classroomId, String classroomName,
			String description) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		map.clear();
		map.put("name", classroomName);
		map.put("description", description);
		if (checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection(CLASSROOMS).document(classroomId).update(map);
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					return getClassroomAfterRequest(classroomId);
				}

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			response.setStatus(ResponseUtils.FORBIDDEN);
			ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/updateClassroom");
		}

		return null;
	}

	@Override
	public Map<String, Object> deleteClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection(CLASSROOMS).document(classroomId).update("archived",
					true);
			try {
				WriteResult writeResult = docRef.get();
				if (writeResult != null) {
					Query archiveRef = firestore.collection(TASKS).whereEqualTo("classroomUid", classroomId);
					ApiFuture<QuerySnapshot> archivedReference = archiveRef.get();
					QuerySnapshot archivedSnapshot = archivedReference.get();
					List<Task> tempTasks = archivedSnapshot.toObjects(Task.class);
					Map<String, Object> tasks = new Hashtable<>();
					System.out.println("tasks list doomed for archiving --> " + tasks);
					for (Task task : tempTasks) {
						System.out.println("task with uid " + task.getUid() + " archived");
						taskServiceImpl.archiveTaskFromClassrrom(classroomId, task.getUid());
					}
					return ResponseUtils.generateSuccessString(classroomId);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		} else {
			response.setStatus(ResponseUtils.FORBIDDEN);
			ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/deleteClassroom");
		}
		return null;
	}

	@Override
	public Map<String, Object> getUsersInClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		Map<String, Object> userMap = new HashMap<>();
		System.out.println("map -> " + map);
		DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				Map<String, String> users = (Map<String, String>) documentSnapshot.get("userList");
				for (String userUid : users.keySet()) {
					System.out.println(userUid);
					DocumentReference docRef2 = firestore.collection(USERS).document(userUid);
					ApiFuture<DocumentSnapshot> documentReference2 = docRef2.get();
					DocumentSnapshot documentSnapshot2 = documentReference2.get();
					if (documentSnapshot2.exists()) {
						User tempUser = documentSnapshot2.toObject(User.class);
						System.out.println(tempUser);
						userMap.put(userUid, tempUser);
					}
				}
				return userMap;
			}
			response.setStatus(ResponseUtils.BAD_REQUEST);
			return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, "Error", "/getUsersInClassroom");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> transferClassroom(String token, String classroomId, String newOwnerId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkOwnerShip(classroomId, uid)) {
			if (checkIfUserIsPartOfClassroom(classroomId, newOwnerId)) {
				DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
				try {
					ApiFuture<DocumentSnapshot> documentReference = docRef.get();
					DocumentSnapshot documentSnapshot = documentReference.get();
					if (documentSnapshot.exists()) {
						String newOwnerEmail = findUserByUid(newOwnerId);
						Map<String, Object> newOwnerMap = new HashMap<>();
						newOwnerMap.put("owner", newOwnerEmail);
						newOwnerMap.put("ownerUid", newOwnerId);
						ApiFuture<WriteResult> reference = firestore.collection(CLASSROOMS).document(classroomId)
								.update(newOwnerMap);
						WriteResult writeResult = reference.get();
						if (writeResult != null) {
							return getClassroomAfterRequest(classroomId);
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			response.setStatus(ResponseUtils.BAD_REQUEST);
			return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.USER_NOT_PART_OF_CLASSROOM,
					"/transferClassroom");
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/transferClassroom");
	}

	@Override
	public Map<String, Object> addToClassroom(String token, String classroomId, String email) {
		Map<String, Object> map = findCurrentUser(token);
		Map<String, Object> valuesMap = new HashMap<>();
		String uid = (String) map.get("uid");
			Query query = firestore.collection(USERS).whereEqualTo("email", email);
			try {
				QuerySnapshot querySnapshot = query.get().get();
				List<QueryDocumentSnapshot> list = querySnapshot.getDocuments();
				if (list.size() > 0) {
					QueryDocumentSnapshot user = list.get(0);
					String userUid = (String) user.get("uid");
					System.out.println(userUid + " uid");
					Map<String, String> userList = new HashMap<>();
					userList.put(userUid, email);
					valuesMap.put("userList", userList);
					valuesMap.put("numberOfUsers", FieldValue.increment(1));
					ApiFuture<WriteResult> docRef = firestore.collection(CLASSROOMS).document(classroomId)
							.set(valuesMap, SetOptions.merge());
					WriteResult writeResult = docRef.get();
					if (writeResult != null) {
						taskServiceImpl.getAllTasksForUserAddedWrongly(classroomId, email);
						return getClassroomAfterRequest(classroomId);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/addToClassroom");

	}

	@Override
	public Map<String, Object> leaveClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkIfUserIsPartOfClassroom(classroomId, uid)) {
			try {
				Map<String, Object> userList = new HashMap<>();
				DocumentReference getListRef = firestore.collection(CLASSROOMS).document(classroomId);
				ApiFuture<DocumentSnapshot> documentReference = getListRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					Map<String, Object> existingMap = (Map<String, Object>) documentSnapshot.get("userList");
					if (existingMap.containsKey(uid)) {
						existingMap.remove(uid);
						userList.put("userList", existingMap);
						ApiFuture<WriteResult> writeExisting = firestore.collection(CLASSROOMS).document(classroomId)
								.set(userList);

						WriteResult writeResult = writeExisting.get();
						if (writeResult != null) {
							ApiFuture<WriteResult> writeNumberOfUsers = firestore.collection(CLASSROOMS)
									.document(classroomId).update("numberOfUsers", FieldValue.increment(-1));
							WriteResult writeNumberOfUsersResults = writeNumberOfUsers.get();
							if (writeNumberOfUsersResults != null) {
								return ResponseUtils.generateSuccessString("You left the classroom sucessfully");
							}
						}
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}

		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/leaveClassroom");

	}

	@Override
	public Map<String, Object> removeFromClassroom(String token, String classroomId, String userId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkIfUserIsPartOfClassroom(classroomId, userId)) {
			try {
				Map<String, Object> userList = new HashMap<>();
				DocumentReference getListRef = firestore.collection(CLASSROOMS).document(classroomId);
				ApiFuture<DocumentSnapshot> documentReference = getListRef.get();
				DocumentSnapshot documentSnapshot = documentReference.get();
				if (documentSnapshot.exists()) {
					Map<String, Object> existingMap = (Map<String, Object>) documentSnapshot.get("userList");
					if (existingMap.containsKey(userId)) {
						existingMap.remove(userId);
						userList.put("userList", existingMap);
						ApiFuture<WriteResult> writeExisting = firestore.collection(CLASSROOMS).document(classroomId)
								.update(userList);
						WriteResult writeResult = writeExisting.get();
						if (writeResult != null) {
							ApiFuture<WriteResult> writeNumberOfUsers = firestore.collection(CLASSROOMS)
									.document(classroomId).update("numberOfUsers", FieldValue.increment(-1));
							WriteResult writeNumberOfUsersResults = writeNumberOfUsers.get();
							if (writeNumberOfUsersResults != null) {
//								taskServiceImpl.getAllTasksForUserAddedWrongly(classroomId, newUserEmail);
								return ResponseUtils.generateSuccessString("You left the classroom sucessfully");
							}
						}
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/removeFromClassroom");
	}

	private Map<String, Object> getClassroomAfterRequest(String uid) {
		DocumentReference docRef = firestore.collection(CLASSROOMS).document(uid);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				Classroom classroom = documentSnapshot.toObject(Classroom.class);
				Map<String, Object> map = new HashMap<>();
				map.put(uid, classroom);
				return map;
			}
			response.setStatus(ResponseUtils.BAD_REQUEST);
			return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, "Classroom not found", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> getUserAfterRequest(String uid) {
		DocumentReference docRef = firestore.collection(USERS).document(uid);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Classroom classroom = documentSnapshot.toObject(Classroom.class);
			Map<String, Object> map = new HashMap<>();
			map.put(uid, classroom);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String findUserByUid(String newOwnerId) {
		try {
			DocumentReference documentReference = firestore.collection(USERS).document(newOwnerId);
			ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
			DocumentSnapshot documentSnapshot = apiFuture.get();
			if (documentSnapshot.exists()) {
				return (String) documentSnapshot.get("email");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean checkOwnerShip(String classroomId, String uid) {
		try {
			DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			String ownerUid = (String) documentSnapshot.get("ownerUid");
			if (ownerUid.equalsIgnoreCase(uid)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkIfUserIsPartOfClassroom(String classroomId, String uid) {
		try {
			DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Map<String, String> users = (Map<String, String>) documentSnapshot.get("userList");
			if (users.size() > 0 && users.containsKey(uid)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Map<String, String> getUsersInClassroomNoToken(String classroomId) {
		DocumentReference docRef = firestore.collection(CLASSROOMS).document(classroomId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				Map<String, String> users = (Map<String, String>) documentSnapshot.get("userList");
				return users;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
