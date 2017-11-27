package com.inovision.apitest.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;

import com.inovision.apitest.controllers.TestInstanceController;
import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestInstanceManager;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValueType;

public class TestTestCaseController extends junit.framework.TestCase {
	
	public void testgetTestInstanceCURL() {
		String expected = "curl -k -i -X GET  -H \"Content-Type: application/json\" -H \"Accept: application/json\" -d '' \"https://de3scvsecureapi00.at.at.cox.net:8443/mso/customer/1.0/purchase/account/1234/all?purchaseType=&startDate=&endDate=&revoked=\"";
		int hostid = 10;
		int testcaseid = 100;
		int testcaseinstanceid = 101;
		
		HostDAO hostdao = EasyMock.createMock(HostDAO.class);
		Host host = new Host();
		host.setHostname("de3scvsecureapi00.at.at.cox.net");
		host.setPort(8443);
		host.setSecureHttp(true);
		EasyMock.expect(hostdao.getHost(hostid)).andReturn(host);
		
		TestCaseDAO dao = EasyMock.createMock(TestCaseDAO.class);
		TestCase tc = new TestCase();
		tc.setId(1);
		tc.setHttpHeaders(Arrays.asList(new HttpHeader(tc, "Content-Type", "application/json"), new HttpHeader(tc, "Accept", "application/json")));
		tc.setRestUrl("/mso/customer/1.0/purchase/account/{accountNumber}/all?purchaseType=&startDate=&endDate=&revoked=");
		EasyMock.expect(dao.getTestCase(testcaseid)).andReturn(tc);
		
		TestCaseInstance tci = new TestCaseInstance();
		tci.setId(testcaseinstanceid);
		tci.setTestCaseId(testcaseid);
		List <TestCaseValue> testCaseValues = new ArrayList<TestCaseValue>();
		tci.setTestCaseValues(testCaseValues);
		testCaseValues.add(new TestCaseValue("accountNumber", "1234", ValueType.PATHPARAM));
		TestInstanceManager testInstanceManager = EasyMock.createMock(TestInstanceManager.class);
		
		EasyMock.expect(testInstanceManager.getTestCaseInstance(testcaseinstanceid)).andReturn(tci);
		
		TestInstanceController tcc = new TestInstanceController();
		tcc.setTestInstanceManager(testInstanceManager);
		tcc.setTestCaseDAO(dao);
		tcc.setHostDAO(hostdao);
		tcc.setTestRunManager(new TestRunManager());

		EasyMock.replay(dao, hostdao, testInstanceManager);
		String curl = tcc.getTestInstanceCURL(testcaseinstanceid, hostid);		
		EasyMock.verify(dao, hostdao, testInstanceManager);
		
		assertNotNull(curl);
		assertEquals(expected, curl);
		
		
		
	}

}
