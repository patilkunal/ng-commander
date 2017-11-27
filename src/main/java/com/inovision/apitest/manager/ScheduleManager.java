package com.inovision.apitest.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;

import com.inovision.apitest.dao.ScheduleDAO;
import com.inovision.apitest.model.Schedule;
import com.inovision.apitest.scheduler.TestCaseRunnerJob;

/**
 * Schedule Manager to schedule and unschedule the test case in quartz scheduler
 * Since this class is configured as <code>ServletConfigAware</code>, it starts quartz
 * scehduler when application starts
 * 
 * @author kunpatil
 *
 */

@Component
public class ScheduleManager implements ServletConfigAware {
	
	public static final String SCHEDULE_JOB_DATA = "SCHEDULE_DATA";

	private ScheduleDAO scheduleDao;	
	private ServletConfig servletConfig;
	private Scheduler quartzScheduler = null;
	private String quartzInstanceName;
	
	private static final Logger LOGGER = Logger.getLogger(ScheduleManager.class);
	
	@Autowired
	public void setScheduleDao(ScheduleDAO scheduleDao) {
		this.scheduleDao = scheduleDao;
	}
	
	public List<Schedule> getScheduleList() {
		return scheduleDao.getScheduleList();
	}
	
	public Schedule getSchedule(int id) {
		return scheduleDao.getSchedule(id);
	}
	
	@Value("${org.quartz.scheduler.instanceName}")
	public void setQuartzInstanceName(String name) {
		LOGGER.info("Setting Quartz Instance Name : " + name);
		this.quartzInstanceName = name;
	}
	
	public Schedule saveSchedule(Schedule schedule) {
		//while saving schedule, if it has TestCaseInstances and is active, schedule it
		//not need to remove if it is new schedule id == -1
		if((schedule.getId() > -1) && !schedule.isActive()) {
			removeQuartzSchedule(schedule);
		} else if(schedule.isActive()) {
			try {
				createQuartzSchedule(schedule);
			} catch(SchedulerException sce) {
				LOGGER.error("Unable to create quartz job for schedule : " + schedule, sce);
			}
		}
		return scheduleDao.saveSchedule(schedule);
	}
	
	public Schedule deleteSchedule(int id) {
		//to delete schedule, unschedule it from Quartz
		Schedule schedule = getSchedule(id);
		removeQuartzSchedule(schedule);
		return scheduleDao.deleteSchedule(id);
	}
	
	public List<Schedule> getScheduledTestCases() {
		List<Schedule> list = new ArrayList<Schedule>(1);
		try {
			List<JobExecutionContext> jobs = quartzScheduler.getCurrentlyExecutingJobs();
			for(JobExecutionContext job : jobs) {
//				Schedule schedule = new Schedule();
//				
//				JobDetail jobdetail = job.getJobDetail();
//				JobDataMap datamap = jobdetail.getJobDataMap();
//				List<TestCaseInstance> testinstances = (List<TestCaseInstance>)datamap.get("TESTCASE_INSTANCES");
//				schedule.setTestCaseInstances(testinstances);
//				
//				JobKey jobkey = jobdetail.getKey();
//				schedule.setName(jobkey.getName());
				
				CronTrigger crontrigger = (CronTrigger) job.getTrigger();
				//Get the schedule object from the trigger jobdata map 
				Schedule schedule = (Schedule)crontrigger.getJobDataMap().get(SCHEDULE_JOB_DATA);
				schedule.setCronExpression(crontrigger.getCronExpression());
				
				list.add(schedule);
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}
	
	@PostConstruct
	public void initialize() {
		ServletContext ctx = servletConfig.getServletContext();
		StdSchedulerFactory factory = (StdSchedulerFactory) ctx
				                 .getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);
		try {
			quartzScheduler = factory.getScheduler(quartzInstanceName);
		} catch (SchedulerException e) {
			LOGGER.error("Erroring in getting Quartz scheduler object");
		}
		
	}
	
	public boolean pauseScheduler() {
		try {
			quartzScheduler.standby();
			return true;
		} catch (SchedulerException e) {
			LOGGER.error("Error while pausing scheduler", e);
			return false;
		}
	}
	
	public boolean resumeScheduler() {
		try {
			quartzScheduler.start();
			return true;
		} catch (SchedulerException e) {
			LOGGER.error("Error while resuming scheduler", e);
			return false;
		}
	}
	
	private void createQuartzSchedule(Schedule schedule) throws SchedulerException {
		TriggerKey triggerKey = getTriggerKey(schedule);

		boolean exists = false;
		try {
			exists = quartzScheduler.checkExists(triggerKey);
		} catch (SchedulerException e) {
			LOGGER.warn("Check exist trigger query failed! Assuming false.");
		}
		
		if(exists) removeQuartzSchedule(schedule);
		
		//We set jobdatamap on trigger rather than JOB
		JobDetail jobdetail = JobBuilder.newJob(TestCaseRunnerJob.class)
			.withIdentity("JOB_" + schedule.getId())
			.build();
		
		CronScheduleBuilder csb = CronScheduleBuilder
							.cronSchedule(schedule.getCronExpression())
							.withMisfireHandlingInstructionIgnoreMisfires();

		JobDataMap trigJobDataMap = new JobDataMap();
		trigJobDataMap.put(SCHEDULE_JOB_DATA, schedule);

		Trigger trigger = TriggerBuilder.newTrigger()
							.withIdentity(triggerKey)
							.withSchedule(csb)
							.usingJobData(trigJobDataMap) //JOB Data map is set on trigger
							.build();
		quartzScheduler.scheduleJob(jobdetail, trigger);
		
	}
	
	public String getSchedulerStatus() {
		try {
			return String.valueOf(!quartzScheduler.isInStandbyMode());
		} catch (SchedulerException e) {
			LOGGER.error("Error getting scheduler status", e);
			return "true";
		}
	}
	
	public boolean validateCron(String cron) {
		return CronExpression.isValidExpression(cron);
	}
	
	private void removeQuartzSchedule(Schedule schedule) {
		TriggerKey triggerKey = getTriggerKey(schedule);
		boolean exists = false;
		try {
			exists = quartzScheduler.checkExists(triggerKey);
		} catch (SchedulerException e) {
			LOGGER.warn("Check exist trigger query failed! Assuming false.");
		}
		//delete this trigger
		if(exists) {
			LOGGER.info("Removing existing trigger for " + schedule);
			try {
				quartzScheduler.unscheduleJob(triggerKey);
			} catch (SchedulerException e) {
				LOGGER.error("Unable to remove schedule for " + schedule, e);
			}
		}
		
	}
	
	private TriggerKey getTriggerKey(Schedule schedule) {
		return new TriggerKey("TRIGGER_" + schedule.getId());
	}
	
}
