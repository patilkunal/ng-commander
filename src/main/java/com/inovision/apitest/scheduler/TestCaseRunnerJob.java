package com.inovision.apitest.scheduler;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.inovision.apitest.manager.ScheduleManager;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Schedule;
import com.inovision.apitest.model.TestCaseInstance;

public class TestCaseRunnerJob implements Job {
	
	private static final Logger LOGGER = Logger.getLogger(TestCaseRunnerJob.class);

	private TestRunManager testRunManager;
	
	@Autowired
	public void setTestRunManager(TestRunManager testRunManager) {
		this.testRunManager = testRunManager;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobdata = context.getTrigger().getJobDataMap();
		Schedule schedule = (Schedule) jobdata.get(ScheduleManager.SCHEDULE_JOB_DATA);
		List<TestCaseInstance> testInstances = schedule.getTestCaseInstances();
		if(testInstances != null) {
			for(TestCaseInstance testInstance : testInstances) {
				testRunManager.executeTest(testInstance.getId(), schedule.getHostId());
			}
		}
		LOGGER.info("Going to run following schedule : " + schedule);
	}

}
