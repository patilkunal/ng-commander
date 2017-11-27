package com.inovision.apitest.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseRunFilter;
import com.inovision.apitest.model.TestResult;

@Controller
public class TestRunController {
	
	private static final Logger LOGGER = Logger.getLogger(TestRunController.class);
	
	private TestCaseDAO testCaseDAO;
	private TestRunManager testRunManager;
	
	@Autowired
	public void setTestRunManager(TestRunManager testRunManager) {
		this.testRunManager = testRunManager;
	}
	
	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}
	
	/**
	 * Called when user submits modified Test Case Instance, but not necessarily saved it
	 * 
	 * @param hostid
	 * @param testCase
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/executeTest/{hostId}", produces="application/json", consumes="application/json")
	public @ResponseBody TestResult executeTest(@PathVariable("hostId") int hostid, @RequestBody TestCase testCase) throws Exception {
		return testRunManager.executeTest(testCase, -1, hostid);
	}

	/**
	 * Called when user submits modified Test Case Instance, but not necessarily saved it
	 * 
	 * @param hostid
	 * @param testCase
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.POST, value="/executeTestCase/{hostId}", produces="application/json", consumes="application/json")
	public @ResponseBody TestResult executeTestCaseUnsavedInstance(@PathVariable("hostId") int hostid, @RequestBody TestCaseInstance testCaseInstance) throws Exception {
		return testRunManager.executeTestInstance(testCaseInstance, hostid);
	}
	
	/**
	 * Executes Test case instance for given testInstanceId on Host specified by hostId
	 * 
	 * @param hostid Host id on which the test needs to execute
	 * @param testInstanceId the Test instance id
	 * @return TestResult 
	 * @throws Exception
	 */
	@RequestMapping(method=RequestMethod.GET, value="/executeTest/{hostId}/{testinstanceid}", produces="application/json")
	public @ResponseBody TestResult executeTest(@PathVariable("hostId") int hostid, @PathVariable("testinstanceid") int testInstanceId) throws Exception {
		return testRunManager.executeTest(testInstanceId, hostid);
	}
	
	@RequestMapping(produces="application/json", value="/testruns/category/{categoryId}", method=RequestMethod.GET)
	public @ResponseBody List<TestCaseRun> getTestCaseRuns(@PathVariable("categoryId") int categoryId) {
		TestCaseRunFilter filter = new TestCaseRunFilter();
		filter.setTestCaseCategoryId(categoryId);
		return testCaseDAO.getTestCaseRuns(filter);
	}
	
	@RequestMapping(value="/testruns/history/{count}", method=RequestMethod.GET)
	public @ResponseBody List<TestCaseRun> getTestCaseRunHistory(@PathVariable("count") int count) {
		TestCaseRunFilter filter = new TestCaseRunFilter();
		filter.setHistoryCount(count);
		return testCaseDAO.getTestCaseRuns(filter);
	}
	
	@RequestMapping(value="/testruns/{id}", method=RequestMethod.GET)
	public @ResponseBody TestCaseRun getTestCaseRun(@PathVariable("id") int id) {		
		TestCaseRun run = testCaseDAO.getTestRun(id);
		return run;
	}
	
}
