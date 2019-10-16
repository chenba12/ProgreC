package com.progresee.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.progresee.app.beans.Completed;

public interface CompletedRepository extends JpaRepository<Completed, Long>{
	
	public Completed findCompletedById(long id);
	

}
