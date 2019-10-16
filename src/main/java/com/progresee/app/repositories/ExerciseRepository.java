package com.progresee.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.progresee.app.beans.Exercise;

@CrossOrigin(origins = "http://localhost:4200")
public interface ExerciseRepository extends JpaRepository<Exercise , Long>{
	
	public Exercise findExerciseById(long id);

}
