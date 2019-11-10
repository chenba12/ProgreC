package com.progresee.app.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulingTaskService {

	  @Scheduled(fixedRate = 5000000)
	    public void deleteArchivedBeans() {
	       System.out.println("deleting");
	    }
}
