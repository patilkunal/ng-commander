package com.inovision.apitest.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inovision.apitest.dao.HttpHeadersDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;

@Component
public class TestInstanceManager {

	private TestCaseDAO testCaseDAO;
	private HttpHeadersDAO httpHeaderDAO;
	
	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}

	@Autowired
	public void setHttpHeaderDAO(HttpHeadersDAO httpHeaderDAO) {
		this.httpHeaderDAO = httpHeaderDAO;
	}
		
	public TestCaseInstance getTestCaseInstance(int id) {
			TestCaseInstance tci = testCaseDAO.getTestCaseInstance(id);
			appendHttpHeaders(tci);
			return tci;
	}

	//consolidates HTTP headers from category, testcase and instance level to this instance
	private void appendHttpHeaders(TestCaseInstance tci) {
		TestCase tc = testCaseDAO.getTestCase(tci.getTestCaseId());
		List<HttpHeader> headers = httpHeaderDAO.getHeaders(tc);
		headers.addAll(httpHeaderDAO.getHeaders(new TestCaseCategory(tc.getTestCategoryId())));
		tci.setHttpHeaders(headers);		
	}
	
}
