package com.inovision.apitest.manager;

import junit.framework.TestCase;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.springframework.http.HttpMethod;

import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpContentType;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.TestResult;
import com.inovision.apitest.model.ValidationType;
import com.inovision.apitest.util.RestHttpClient;

public class TestTestRunManager extends TestCase {

	/*
	 * Test JSON result template.
	 */
	public void testJSONResultOutput() throws Exception {

		String expectedjsonString = "{ name: \"john\", age: 10 }";
		String actualjsonString = "{ age: 20, name: \"smith\" }";
		int hostid = 1;
		int testCaseInstanceId = 1;
		com.inovision.apitest.model.TestCase testCase = new com.inovision.apitest.model.TestCase();
		testCase.setName("junit test");
		testCase.setMethod(HttpMethod.GET);
		testCase.setResponseType(HttpContentType.JSON);
		testCase.setValidateType(ValidationType.JSON);
		testCase.setRestUrl("/context/rest/user/{userid}");
		Host host = new Host();
		host.setId(hostid);
		
		TestCaseInstance tci = new TestCaseInstance();
		tci.setId(testCaseInstanceId);
		tci.setTestCaseId(1);
		tci.setValidateOutput(true);
		tci.setOutputTemplate(expectedjsonString);
		tci.setTestCaseValues(Arrays.asList(new TestCaseValue("userid", "john")));
		
		TestCaseDAO dao = EasyMock.createMock(TestCaseDAO.class);
		HostDAO hostdao = EasyMock.createMock(HostDAO.class);
		TestInstanceManager testInstanceManager = EasyMock.createMock(TestInstanceManager.class);
		
		EasyMock.expect(hostdao.getHost(hostid)).andReturn(host);
		EasyMock.expect(testInstanceManager.getTestCaseInstance(testCaseInstanceId)).andReturn(tci);
		//TestCaseRun run = new TestCaseRun();
		EasyMock.expect(dao.saveTestRun(EasyMock.anyObject(TestCaseRun.class))).andReturn(null);
		RestHttpClient client = EasyMock.createMock(RestHttpClient.class);
		TestResult testResult = new TestResult();
		testResult.setContentType(HttpContentType.JSON);
		testResult.setResult(actualjsonString);
		testResult.setSuccess(true);
		EasyMock.expect(client.doHttpRequest(testCase, host, null)).andReturn(testResult);
		
		TestRunManager testRunManager = new TestRunManager();
		testRunManager.setTestInstanceManager(testInstanceManager);
		testRunManager.setTestCaseDAO(dao);
		testRunManager.setHostDAO(hostdao);
		testRunManager.setRestClient(client);
		
		EasyMock.replay(dao, client, hostdao, testInstanceManager);
		TestResult result = testRunManager.executeTest(testCase, testCaseInstanceId, hostid);
		EasyMock.verify(dao, client, hostdao, testInstanceManager);
		assertTrue(result.isSuccess());
		
	}
	

	/*
	 * Test TEXT regex comparator.
	 */
	public void testTEXTResultOutput() throws Exception {

		String regexPattern = "true|True|TRUE";
		String actualjsonString = "true";
		int hostid = 1;
		int testCaseInstanceId = 1;
		com.inovision.apitest.model.TestCase testCase = new com.inovision.apitest.model.TestCase();
		testCase.setName("junit test");
		testCase.setMethod(HttpMethod.GET);
		testCase.setResponseType(HttpContentType.TEXT);
		testCase.setValidateType(ValidationType.REGEX);
		testCase.setRestUrl("/context/rest/user/{userid}");
		
		Host host = new Host();
		host.setId(hostid);
		TestInstanceManager testInstanceManager = EasyMock.createMock(TestInstanceManager.class);
		
		TestCaseInstance tci = new TestCaseInstance();
		tci.setId(testCaseInstanceId);
		tci.setTestCaseId(1);
		tci.setValidateOutput(true);
		tci.setOutputTemplate(regexPattern);
		
		TestCaseDAO dao = EasyMock.createMock(TestCaseDAO.class);
		HostDAO hostdao = EasyMock.createMock(HostDAO.class);

		EasyMock.expect(hostdao.getHost(hostid)).andReturn(host);
		EasyMock.expect(testInstanceManager.getTestCaseInstance(testCaseInstanceId)).andReturn(tci);
		//TestCaseRun run = new TestCaseRun();
		EasyMock.expect(dao.saveTestRun(EasyMock.anyObject(TestCaseRun.class))).andReturn(null);
		RestHttpClient client = EasyMock.createMock(RestHttpClient.class);
		TestResult testResult = new TestResult();
		testResult.setContentType(HttpContentType.TEXT);
		testResult.setResult(actualjsonString);
		testResult.setSuccess(true);
		EasyMock.expect(client.doHttpRequest(testCase, host, null)).andReturn(testResult);
		
		TestRunManager testRunManager = new TestRunManager();
		testRunManager.setTestInstanceManager(testInstanceManager);
		testRunManager.setTestCaseDAO(dao);
		testRunManager.setHostDAO(hostdao);
		testRunManager.setRestClient(client);
		
		EasyMock.replay(dao, client, hostdao, testInstanceManager);
		TestResult result = testRunManager.executeTest(testCase, testCaseInstanceId, hostid);
		EasyMock.verify(dao, client, hostdao, testInstanceManager);
		assertTrue(result.isSuccess());
		
	}
	
}
