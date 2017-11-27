package com.inovision.apitest.manager;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.HttpHeadersDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestResult;
import com.inovision.apitest.util.RestHttpClient;
import com.inovision.apitest.util.ResultComparator;
import com.inovision.apitest.util.ResultComparatorFactory;
import com.inovision.apitest.util.TokenMerger;

@Component
public class TestRunManager {

	private static final Logger logger = Logger.getLogger(TestRunManager.class);
			
	private TestCaseDAO testCaseDAO;
	private HostDAO hostDAO;
	private TestInstanceManager testInstanceManager; 
	
	private RestHttpClient restClient;
	
	private static final int MAX_RESULT_LENGTH = 4070;
	private static final String TRUNCATED = "[RESULT TRUNCATED]";
	private static final String BLANK = "";

	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}
	
	@Autowired
	public void setRestClient(RestHttpClient restClient) {
		this.restClient = restClient;
	}
	
	@Autowired
	public void setHostDAO(HostDAO hostDAO) {
		this.hostDAO = hostDAO;
	}
	
	@Autowired
	public void setTestInstanceManager(TestInstanceManager testInstanceManager) {
		this.testInstanceManager = testInstanceManager;
	}

	public TestCase mergeTestValues(TestCase tc, TestCaseInstance tci) {
		TestCase newtc = TokenMerger.mergeTokenValues(tc, tci); 
		return newtc;
	}
	
	/*
	private String mergeUrlValues(String template, List<TestCaseValue> values) {
		String mergeString = template;
		if((values != null) && (values.size() > 0)) {
			for(TestCaseValue tcv : values) {
				String name = tcv.getName();
				String replacestr = String.format("\\{%s\\}", name);
				String value = "";
				if(tcv.getValue() != null) {
					try {
						value = java.net.URLEncoder.encode(tcv.getValue(), "ISO-8859-1");
					} catch(UnsupportedEncodingException e) {};
				}
				mergeString = mergeString.replaceAll(replacestr, value);
			}
		}
		return mergeString;
	}
	
	private String mergeDataValues(String template, List<TestCaseValue> values) {
		String mergeString = template;
		if((template != null) && (values != null) && (values.size() > 0)) {
			for(TestCaseValue tcv : values) {
				String name = tcv.getName();
				String replacestr = null;
				if(tcv.isRequired()) {
					replacestr = String.format("{%s}", name);
				} else {
					replacestr = String.format("{?%s}", name);
				}
				mergeString = mergeString.replaceAll(replacestr, tcv.getValue());
			}
		}
		return mergeString;
	}
	*/
	
	public void saveTestRunResult(TestResult result, Host host) {
		if(result != null) {
			TestCaseRun run = new TestCaseRun();
			run.setRunDate(new Date());
			run.setSuccess(result.isSuccess());
			run.setTestCaseInstance(new TestCaseInstance(result.getTestInstanceId()));
			run.setHost(host);
			run.setError(result.getError());
			run.setContentType(result.getContentType());
			if((result.getResult() != null) && (result.getResult().length() > MAX_RESULT_LENGTH)) {
				run.setResult(result.getResult().substring(0, MAX_RESULT_LENGTH) + TRUNCATED);
			} else {
				run.setResult(result.getResult());
			}
			run.setReturnCode(result.getReturnCode());
			testCaseDAO.saveTestRun(run);
			logger.debug("Successfully saved result " + result);
		}
	}
	
	public TestResult executeTest(int testInstanceId, int hostid) {
		TestCaseInstance tci = testInstanceManager.getTestCaseInstance(testInstanceId);
		TestCase tc = testCaseDAO.getTestCase(tci.getTestCaseId());
		Host host = hostDAO.getHost(hostid);
		TestResult result = executeTest(tc, tci, host);
		return result;
	}
	
	public TestResult executeTestInstance(TestCaseInstance tci, int hostid) {
		Host host = hostDAO.getHost(hostid);
		TestCase testCase = testCaseDAO.getTestCase(tci.getTestCaseId());
		tci.setAllowBlankOutput(testCase.isAllowBlankOutput());
		tci.appendHttpHeaders(testCase.getHttpHeaders());
		tci.setOutputTemplate(testCase.getOutputTemplate());
		tci.setValidateOutput(testCase.isValidateOutput());
		tci.setValidateType(testCase.getValidateType());
		return executeTest(testCase, tci, host);
	}
	
	public TestResult executeTest(TestCase testCase, int testCaseInstanceId, int hostid) {
		Host host = hostDAO.getHost(hostid);
		TestCaseInstance tci = testInstanceManager.getTestCaseInstance(testCaseInstanceId);
		return executeTest(testCase, tci, host);
	}
	
	public TestResult executeTest(TestCase tc,  TestCaseInstance tci, Host host) {
		TestCase testCase = mergeTestValues(tc, tci);
		logger.info("Executing test : " + testCase + " on host " + host);
		TestResult result = null;
		try {
			result = restClient.doHttpRequest(testCase, host, tci.getHttpHeaders());
			if(result.isSuccess() && !testCase.getResponseType().equals(result.getContentType())) {
				result.setSuccess(false);
				result.setError(String.format("Expected Content-Type '%1$s' does not match returned '%2s'", testCase.getResponseType().getType(), result.getContentType().getType()) );
			}
		} catch(Throwable t) {
			logger.error("Error in executing test : " + testCase, t);
			result = new TestResult();
			result.setSuccess(false);
			result.setReturnCode(-1);
			result.setError(t.getMessage());
		}
		result.setTestInstanceId(tci.getId());
		result.setHostId(host.getId());
		if(tci.isValidateOutput() && result.isSuccess()) { 
			ResultComparator comp = ResultComparatorFactory.getResultComparator(testCase.getValidateType());
			if(comp != null) {
				boolean match = comp.compare(tci.getOutputTemplate(), result.getResult());
				result.setSuccess(match);
				if(!match) {
					StringBuilder buf = new StringBuilder();
					for(String error : comp.getErrors()) {
						buf.append(error);
						buf.append(";;");
					}
					result.setError(buf.toString());
				} else {
					logger.info("Test case output match expected output");
				}
			} else {
				String error = "Unable to compare result. Result comparator is not implemented for " + testCase.getResponseType();
				logger.error(error);
				result.setError(error);
			}
		}
		if(tci.getId() > -1) {
			logger.debug("Saving TestResult " + result);
			try {
				saveTestRunResult(result, host);
			} catch(Throwable t) {
				logger.error("Unable to save test case result", t);
			}
		} else {
			logger.debug("Not saving result for adhoc run: " + result);
		}
		return result;
	}
	
	public String formatHeaders(List<HttpHeader> headers) {
		if(headers != null && headers.size() > 0) {
			StringBuilder buf = new StringBuilder();
			for(HttpHeader h : headers) {
				buf.append(String.format(" -H \"%s: %s\"", h.getName(), h.getValue()));
			}
			return buf.toString();
		} else {
			return BLANK;
		}
	}
	
}
