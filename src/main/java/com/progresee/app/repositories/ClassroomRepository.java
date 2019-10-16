package com.progresee.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.progresee.app.beans.Classroom;

@CrossOrigin(origins = "http://localhost:4200")
public interface ClassroomRepository extends JpaRepository<Classroom, Long>  {
	
	public List<Classroom> findClassRoomById(long id);
	

}
