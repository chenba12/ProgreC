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
		FirebaseToken decodedToken;
		Map<String, Object> returnedMap = new Hashtable<>();
		try {
			decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			System.out.println("decoded token is -> " + decodedToken);
			String uid = decodedToken.getUid();
			DocumentReference documentReference = firestore.collection("users").document(uid);
			// asynchronously retrieve the document
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
		map.remove("uid");
		System.out.println("mapWITHOUTUID -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("users").document(uid).set(user);
		try {
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return getUserAfterRequest(uid);
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
		DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			List<String> users = (List<String>) documentSnapshot.get("userList");
			if (users.contains(uid)) {
				return documentSnapshot.getData();
			}
			response.setStatus(400);
			return ResponseUtils.generateErrorCode(400, "you are not part of this classroom", "/getClassroom");
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
			return classrooms;

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
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
		return null;
	}

	@Override
	public Map<String, Object> updateClassroom(String token, String classroomId, String classroomName) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("name",
				classroomName);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult.getUpdateTime()!=null) {
				return getClassroomAfterRequest(classroomId);
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> deleteClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).delete();
		try {
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return ResponseUtils.generateSuccessString("classroom deleted");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
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
			response.setStatus(400);
			return ResponseUtils.generateErrorCode(400, "empty classroom madafaka", "/getUsersInClassroom");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> transferClassroom(String token, String classroomId, String newOwnerId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String email = (String) map.get("email");
		DocumentReference docRef = firestore.collection("classrooms").document(classroomId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			System.out.println("documentReference ->" + documentReference);
			DocumentSnapshot documentSnapshot = documentReference.get();
			System.out.println("documentSnapshot ->" + documentSnapshot);
			if (documentSnapshot.exists()) {
				String owner = (String) documentSnapshot.get("owner");
				String newOwnerEmail = findUserByUid(newOwnerId);
				if (owner.equalsIgnoreCase(email)) {
					ApiFuture<WriteResult> reference = firestore.collection("classrooms").document(classroomId)
							.update("owner", newOwnerEmail);
					WriteResult fuReference = reference.get();
					if (fuReference.getUpdateTime() != null) {
						return ResponseUtils.generateSuccessString("transfer successfullz");
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(400);
		return ResponseUtils.generateErrorCode(400, "transferClassroomError", "/transferClassroom");
	}

	@Override
	public Map<String, Object> addToClassroom(String token, String classroomId, String userId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("userList",
				FieldValue.arrayUnion(userId));
		try {
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return ResponseUtils.generateSuccessString("Added user sucessfully");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ResponseUtils.generateErrorCode(400, "failed to add user to classroom", "/addToClassroom");
	}

	@Override
	public Map<String, Object> leaveClassroom(String token, String classroomId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("userList",
				FieldValue.arrayRemove(uid));
		try {
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return ResponseUtils.generateSuccessString("You left the classroom sucessfully");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ResponseUtils.generateErrorCode(400, "failed to leave  classroom", "/leaveClassroom");

	}

	@Override
	public Map<String, Object> removeFromClassroom(String token, String classroomId, String userId) {
		Map<String, Object> map = findCurrentUser(token);
		System.out.println("map -> " + map);

		System.out.println("***************");
		System.out.println(classroomId);
		try {
			ApiFuture<WriteResult> docRef = firestore.collection("classrooms").document(classroomId).update("userList",
					FieldValue.arrayRemove(userId));
			WriteResult writeResult = docRef.get();
			System.out.println("writeResult ->" + writeResult);
			return ResponseUtils.generateSuccessString("Removed user sucessfully");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ResponseUtils.generateErrorCode(400, "Failed to leave  classroom", "/leaveClassroom");
	}

	@Override
	public Map<String, Object> findCurrentUser(String token) {
		FirebaseToken decodedToken;
		Map<String, Object> returnedMap = new Hashtable<>();
		try {
			decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
			System.out.println("decoded token is -> " + decodedToken);
			String uid = decodedToken.getUid();
			DocumentReference documentReference = firestore.collection("users").document(uid);
			// asynchronously retrieve the document
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
			Classroom classroom = documentSnapshot.toObject(Classroom.class);
			Map<String, Object> map = new Hashtable<>();
			map.put(uid, classroom);
			return map;
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
}
