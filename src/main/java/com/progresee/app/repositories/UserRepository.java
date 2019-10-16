package com.progresee.app.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.progresee.app.beans.User;

@CrossOrigin(origins = "http://localhost:4200")
public interface UserRepository extends JpaRepository<User, Long> {
	
	public User findByEmail(String email);

	public User findUserById(long id);

	public User deleteById(long id);
	
	public User findByUid(String uid);
	
	@Query(value="Select u.user_id FROM user_classrooms as u WHERE classrooms_id = ?1", nativeQuery=true)
	public List<Long> findUserInClassroom(long classRoomId);

}
