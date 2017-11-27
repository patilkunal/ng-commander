package com.inovision.apitest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestInstanceManager;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;

@Controller
@RequestMapping("/testinstance")
public class TestInstanceController {

	//args in order of action, header, data, URL
	private static final String CURL_FORMAT = "curl -k -i -X %s %s -d '%s' \"%s\"";
		
	private TestCaseDAO testCaseDAO;
	private TestRunManager testRunManager;
	private HostDAO hostDAO;
	private TestInstanceManager testInstanceManager;

	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}

	@Autowired
	public void setTestRunManager(TestRunManager testRunManager) {
		this.testRunManager = testRunManager;
	}
	
	@Autowired
	public void setHostDAO(HostDAO hostDAO) {
		this.hostDAO = hostDAO;
	}
	
	@Autowired
	public void setTestInstanceManager(TestInstanceManager testInstanceManager) {
		this.testInstanceManager = testInstanceManager;
	}

	@RequestMapping(produces="application/json", value="/category/{categoryId}", method=RequestMethod.GET)
	public @ResponseBody List<TestCaseInstance> getTestCaseInstances(@PathVariable("categoryId") int categoryId) {		
		return testCaseDAO.getTestCaseInstances(categoryId);
	}
	
	@RequestMapping(produces="application/json", value="/{id}", method=RequestMethod.GET)
	public @ResponseBody TestCaseInstance getTestCaseInstance(@PathVariable("id") int id) {		
		return testInstanceManager.getTestCaseInstance(id);
	}
	
	@RequestMapping(produces="application/json", method=RequestMethod.POST)
	public @ResponseBody TestCaseInstance saveTestCaseInstance(@RequestBody TestCaseInstance ti) {
		return testCaseDAO.saveTestCaseInstance(ti);
	}

	@RequestMapping(produces="application/json", value="/{id}", method=RequestMethod.PUT)
	public @ResponseBody TestCaseInstance updateTestCaseInstance(@RequestBody TestCaseInstance ti) {
		return testCaseDAO.saveTestCaseInstance(ti);
	}

	@RequestMapping(produces="application/json", value="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.ACCEPTED)
	public void deleteTestCaseInstance(@PathVariable("id") int id) {
		TestCaseInstance tc = getTestCaseInstance(id);
		testCaseDAO.deleteTestRun(tc);
		testCaseDAO.deleteTestCaseValues(tc);
		testCaseDAO.deleteTestCaseInstance(id);
	}
	
	@RequestMapping(produces="text/plain", value="/{id}/host/{hostid}/curl", method=RequestMethod.GET)
	public @ResponseBody String getTestInstanceCURL(@PathVariable("id") int id, @PathVariable("hostid") int hostid) {
		Host host = hostDAO.getHost(hostid);
		TestCaseInstance ti = getTestCaseInstance(id);
		TestCase tc = testCaseDAO.getTestCase(ti.getTestCaseId());
		TestCase finaltc = testRunManager.mergeTestValues(tc, ti);
		String headers = testRunManager.formatHeaders(finaltc.getHttpHeaders());
		return String.format(CURL_FORMAT, finaltc.getMethod().name(), headers, finaltc.getData() == null ? ""  : finaltc.getData(), host.toUrlFormat() + finaltc.getRestUrl());
	}
	
	@RequestMapping(produces="text/plain", value="/adhoc/curl/host/{hostid}", method=RequestMethod.POST)
	public @ResponseBody String getAdhocTestInstanceCURL(@RequestBody TestCaseInstance ti, @PathVariable("hostid") int hostid) {
		Host host = hostDAO.getHost(hostid);
		TestCase tc = testCaseDAO.getTestCase(ti.getTestCaseId());
		TestCase finaltc = testRunManager.mergeTestValues(tc, ti);
		String headers = testRunManager.formatHeaders(finaltc.getHttpHeaders());
		return String.format(CURL_FORMAT, finaltc.getMethod().name(), headers, finaltc.getData() == null ? ""  : finaltc.getData(), host.toUrlFormat() + finaltc.getRestUrl());
	}
	
}
