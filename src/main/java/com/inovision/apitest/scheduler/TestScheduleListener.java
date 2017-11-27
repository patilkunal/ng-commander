package com.inovision.apitest.scheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestScheduleListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent ctxEvent) {
		//initialize all jobs from the database here
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent ctxEvent) {
		//Shutdown the quartz jobs here
		
	}
}
