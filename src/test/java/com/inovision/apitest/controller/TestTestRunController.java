package com.inovision.apitest.controller;

import java.util.Arrays;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.springframework.http.HttpMethod;

import com.inovision.apitest.controllers.TestRunController;
import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestInstanceManager;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpContentType;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.TestResult;
import com.inovision.apitest.model.ValidationType;
import com.inovision.apitest.util.RestHttpClient;

public class TestTestRunController extends junit.framework.TestCase {

	public void testExecuteTestByHostAndInstance() throws Exception {
		TestCaseInstance tci = new TestCaseInstance(36);
		tci.setAllowBlankOutput(true);
		tci.setName("testExecuteTestByHostAndInstance");
		tci.setOutputTemplate("{ \"results\": [],   \"requestId\": \"E0a76q-tN_-MVJ1h\",   \"error\": null,   \"clientTransactionId\": null,   \"executionTimeInMillis\": 87}");
		tci.setTestCaseId(44);
		tci.setTestCaseValues(Arrays.<TestCaseValue>asList(
				new TestCaseValue("accountNumber", "25b3cad0-4686-11e5-8f17-0050560d019a"),
				new TestCaseValue("purchaseType", null),
				new TestCaseValue("startDate", null),
				new TestCaseValue("endDate", null),
				new TestCaseValue("revoked", null)
				));
		tci.setValidateOutput(true);
		tci.setValidateType(ValidationType.JSON);
		
		TestCase tc = new TestCase();
		tc.setAllowBlankOutput(true);
		tc.setId(44);
		tc.setMethod(HttpMethod.GET);
		tc.setName("Purchase History ALL");
		tc.setOutputTemplate(tci.getOutputTemplate());
		tc.setResponseType(HttpContentType.JSON);
		tc.setRestUrl(" /mso/customer/1.0/purchase/account/{accountNumber}/all?purchaseType=&startDate=&endDate=&revoked=");
		tc.setTestCategoryId(5);
		tc.setValidateOutput(true);
		tc.setValidateType(ValidationType.JSON);
		
		Host host = new Host();
		host.setId(21);
		host.setHostname("de3scvsecureapi00.at.at.cox.net");
		host.setPort(8443);
		host.setSecureHttp(true);
		host.setTestCategoryId(5);
		
		Capture<TestCase> testcaseCapture = EasyMock.newCapture();
		Capture<Host> hostCapture = EasyMock.newCapture();
		Capture<List<HttpHeader>> headerCapture = EasyMock.newCapture();
		RestHttpClient restclient = EasyMock.createMock(RestHttpClient.class);
		TestResult tr = new TestResult();
		tr.setContentType(HttpContentType.JSON);
		tr.setHostId(21);
		
		EasyMock.expect(restclient.doHttpRequest(EasyMock.capture(testcaseCapture), EasyMock.capture(hostCapture), EasyMock.capture(headerCapture))).andReturn(tr);
		
		TestCaseDAO dao = EasyMock.createMock(TestCaseDAO.class);
		HostDAO hostdao = EasyMock.createMock(HostDAO.class);
		TestInstanceManager testInstanceManager = EasyMock.createMock(TestInstanceManager.class);
		
		EasyMock.expect(testInstanceManager.getTestCaseInstance(36)).andReturn(tci).anyTimes();
		EasyMock.expect(hostdao.getHost(21)).andReturn(host);
		EasyMock.expect(dao.getTestCase(44)).andReturn(tc);
		EasyMock.expect(dao.saveTestRun(EasyMock.anyObject(TestCaseRun.class))).andReturn(null);
		
		TestRunManager trm = new TestRunManager();
		trm.setTestInstanceManager(testInstanceManager);
		trm.setTestCaseDAO(dao);
		trm.setHostDAO(hostdao);
		trm.setRestClient(restclient);
		//EasyMock.expect(trm.executeTest(36, 21)).andReturn(tr);

		TestRunController trc = new TestRunController();
		trc.setTestRunManager(trm);
		EasyMock.replay(dao, restclient, hostdao, testInstanceManager);
		trc.executeTest(21, 36);
		EasyMock.verify(dao, restclient, hostdao, testInstanceManager);
		
	}
}
