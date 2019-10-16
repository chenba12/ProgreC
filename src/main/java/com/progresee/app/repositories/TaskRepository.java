package com.progresee.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.progresee.app.beans.Task;

@CrossOrigin(origins = "http://localhost:4200")
public interface TaskRepository extends JpaRepository<Task, Long>{

	public Task deleteById(long id);


}
