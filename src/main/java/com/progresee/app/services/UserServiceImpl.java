package com.progresee.app.services;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.progresee.app.beans.Classroom;
import com.progresee.app.beans.User;
import com.progresee.app.services.dao.UserService;
import com.progresee.app.utils.ResponseUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	Firestore firestore;

	@Autowired
	HttpServletResponse response;

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

		Map<String, Object> returnedMap = new Hashtable<>();
		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			System.out.println("decoded token is -> " + decodedToken);
			String uid = decodedToken.getUid();
			DocumentReference documentReference = firestore.collection("users").document(uid);
			ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
			DocumentSnapshot documentSnapshot = apiFuture.get();
			if (documentSnapshot.exists()) {
				System.out.println("found user " + decodedToken.getName() + "with uid " + decodedToken.getUid());
				returnedMap.put("signedIn", Calendar.getInstance().getTime());
				return documentSnapshot.getData();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> updateUser(String token, User user) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		ApiFuture<WriteResult> docRef = firestore.collection("users").document(uid).set(user);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult.getUpdateTime() != null) {
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
			DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			List<String> users = (List<String>) documentSnapshot.get("userList");
			if (users.contains(uid)) {
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
		Query docRef = firestore.collection("classrooms").whereArrayContains("userList", uid);
		try {
			ApiFuture<QuerySnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			QuerySnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			List<Classroom> tempClassrooms = documentSnapshot.toObjects(Classroom.class);
			System.out.println("classrooms IN LIST---->" + tempClassrooms);
			Map<String, Object> classrooms = new Hashtable<>();
			for (Classroom classroom : tempClassrooms) {
				classrooms.put(classroom.getUid(), classroom);
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
	public Map<String, Object> createClassroom(String token, String classroomName) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		String owner = (String) map.get("email");
		Classroom classroom = new Classroom();
		classroom.setName(classroomName);
		classroom.setOwner(owner);
		classroom.setDateCreated(Calendar.getInstance().getTime());
		classroom.getUserList().add(uid);
		classroom.setOwnerUid(uid);
		String classroomUid = UUID.randomUUID().toString().replace("-", "");
		classroom.setUid(classroomUid);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomUid).set(classroom);
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
	public Map<String, Object> updateClassroom(String token, String classroomId, String classroomName) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("name",
					classroomName);

			try {
				WriteResult writeResult = docRef.get();
				if (writeResult.getUpdateTime() != null) {
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
			ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).delete();
			try {
				WriteResult writeResult = docRef.get();
				System.out.println("writeResult ->" + writeResult);
				return ResponseUtils.generateSuccessString("classroom deleted");
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
		System.out.println("map -> " + map);
		DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			if (documentSnapshot.exists()) {
				List<String> users = (List<String>) documentSnapshot.get("userList");
				map.clear();
				for (int i = 0; i < users.size(); i++) {
					map.put(String.valueOf(i), users.get(i));
				}
				return map;
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
				DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
				try {
					ApiFuture<DocumentSnapshot> documentReference = docRef.get();
					System.out.println("documentReference ->" + documentReference);
					DocumentSnapshot documentSnapshot = documentReference.get();
					System.out.println("documentSnapshot ->" + documentSnapshot);
					if (documentSnapshot.exists()) {
						String newOwnerEmail = findUserByUid(newOwnerId);
							Map<String, Object> newOwnerMap = new Hashtable<>();
							newOwnerMap.put("owner", newOwnerEmail);
							newOwnerMap.put("onwerUid", newOwnerId);
							ApiFuture<WriteResult> reference = firestore.collection("classrooms").document(classroomId)
									.update(newOwnerMap);
							WriteResult fuReference = reference.get();
							if (fuReference.getUpdateTime() != null) {
								return ResponseUtils.generateSuccessString("transfer successfull");
							}
						}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			response.setStatus(ResponseUtils.BAD_REQUEST);
			return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.USER_NOT_PART_OF_CLASSROOM, "/transferClassroom");
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/transferClassroom");
	}

	@Override
	public Map<String, Object> addToClassroom(String token, String classroomId, String userId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (checkOwnerShip(classroomId, uid)) {
			ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("userList",
					FieldValue.arrayUnion(userId));
			try {
				WriteResult writeResult = docRef.get();
				System.out.println("writeResult ->" + writeResult);
				return ResponseUtils.generateSuccessString("Added user sucessfully");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

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
			ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("userList",
					FieldValue.arrayRemove(uid));
			try {
				WriteResult writeResult = docRef.get();
				System.out.println("writeResult ->" + writeResult);
				return ResponseUtils.generateSuccessString("You left the classroom sucessfully");
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
		if (checkOwnerShip(classroomId, uid)) {
			try {
				if (checkIfUserIsPartOfClassroom(classroomId, userId)) {
					ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId)
							.update("userList", FieldValue.arrayRemove(userId));
					WriteResult writeResult = docRef.get();
					if (writeResult.getUpdateTime() != null) {
						return ResponseUtils.generateSuccessString("Removed user sucessfully");
					}
				}
				response.setStatus(ResponseUtils.BAD_REQUEST);
				return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.NOT_PART_OF_CLASSROOM,
						"/removeUser");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/removeUser");
	}

	@Override
	public Map<String, Object> findCurrentUser(String token) {

		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			System.out.println("decoded token is -> " + decodedToken);
			String uid = decodedToken.getUid();
			Map<String, Object> returnedMap = new Hashtable<>();
			DocumentReference documentReference = firestore.collection("users").document(uid);
			ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
			DocumentSnapshot documentSnapshot = apiFuture.get();
			if (documentSnapshot.exists()) {
				System.out.println("found user " + decodedToken.getName() + "with uid " + decodedToken.getUid());
				returnedMap.put("signedIn", FieldValue.serverTimestamp());
				ApiFuture<WriteResult> updatedDocumentReference = firestore.collection("users").document(uid)
						.update(returnedMap);
				if (updatedDocumentReference != null) {
					System.out.println("new sign in time " + updatedDocumentReference);
					returnedMap.clear();
					returnedMap.put("uid", uid);
					returnedMap.put("email", documentSnapshot.getData().get("email"));
					return returnedMap;
				}
			} else {
				User user = new User();
				user.setDateCreated(Calendar.getInstance().getTime());
				user.setSignedIn(Calendar.getInstance().getTime());
				user.setFullName(decodedToken.getName());
				user.setEmail(decodedToken.getEmail());
				user.setProfilePictureUrl(decodedToken.getPicture());
				ApiFuture<WriteResult> docRef = firestore.collection("users").document(uid).set(user);
				if (docRef != null) {
					System.out.println("user added " + user);
					returnedMap.clear();
					returnedMap.put("uid", uid);
					returnedMap.put("email", decodedToken.getEmail());
					return returnedMap;
				}
			}
		} catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> getClassroomAfterRequest(String uid) {
		DocumentReference docRef = firestore.collection("classrooms").document(uid);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			if (documentSnapshot.exists()) {
				Classroom classroom = documentSnapshot.toObject(Classroom.class);
				Map<String, Object> map = new Hashtable<>();
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
		DocumentReference docRef = firestore.collection("users").document(uid);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			Classroom classroom = documentSnapshot.toObject(Classroom.class);
			Map<String, Object> map = new Hashtable<>();
			map.put(uid, classroom);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String findUserByUid(String newOwnerId) {
		try {
			DocumentReference documentReference = firestore.collection("users").document(newOwnerId);
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
			DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
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
			DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			List<String> users = (List<String>) documentSnapshot.get("userList");
			if (users.size() > 0 && users.contains(uid)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
