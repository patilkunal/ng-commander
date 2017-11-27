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

import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpContentType;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseRunFilter;
import com.inovision.apitest.util.TokenParser;

@Controller
public class TestCaseController {

	private TestCaseDAO testCaseDAO;
	
	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}
	
	@RequestMapping(produces="application/json", value="/testcase/{id}", method=RequestMethod.PUT)
	public @ResponseBody TestCase updateTestCase(@RequestBody TestCase testCase) {
		return testCaseDAO.saveTestCase(testCase);
	}

	@RequestMapping(produces="application/json", value="/testcase", method=RequestMethod.POST)
	public @ResponseBody TestCase saveTestCase(@RequestBody TestCase testCase) {
		return testCaseDAO.saveTestCase(testCase);
	}
	
	@RequestMapping(produces="application/json", value="/testcase/{id}", method=RequestMethod.GET)
	public @ResponseBody TestCase getTestCase(@PathVariable("id") int id) {		
		return testCaseDAO.getTestCase(id);
	}

	@RequestMapping(produces="application/json", value="/testcase/category/{categoryid}", method=RequestMethod.GET)
	public @ResponseBody List<TestCase> getTestCases(@PathVariable("categoryid") int categoryId) {		
		return testCaseDAO.getTestCases(categoryId);
	}
	
	@RequestMapping(produces="application/json", value="/testcase/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.ACCEPTED)
	public void deleteTestCase(@PathVariable("id") int id) {
		testCaseDAO.deleteTestCase(id);
	}	
	
	@RequestMapping(produces="application/json", value="/testcase/{id}/testinstance", method=RequestMethod.GET)
	public @ResponseBody TestCaseInstance createTestCaseInstance(@PathVariable("id") int id) {		
		TestCase testCase = testCaseDAO.getTestCase(id);
		TestCaseInstance instance = null;
		if(testCase != null) {
			instance = new TestCaseInstance();
			instance.setTestCaseId(testCase.getId());
			instance.setName(testCase.getName() + " Instance");
			instance.setDescription(testCase.getName() + " Instance");
			instance.setTestCaseValues(TokenParser.parseTokens(testCase.getRestUrl(), testCase.getData()));
			instance.setAllowBlankOutput(testCase.isAllowBlankOutput());
			instance.setValidateOutput(testCase.isValidateOutput());
			instance.setValidateType(testCase.getValidateType());
			instance.setOutputTemplate(testCase.getOutputTemplate());
		}
		return instance;
	}
	
	@RequestMapping(produces="application/json", value="/testcase/{id}/headers", method=RequestMethod.GET)
	public @ResponseBody List<HttpHeader> getTestCaseHttpHeaders(@PathVariable("id") int id) {
		TestCase tc = testCaseDAO.getTestCase(id);
		return tc.getHttpHeaders();
	}
	
	@RequestMapping(produces="application/json", value="/contenttypes", method=RequestMethod.GET)
	public @ResponseBody HttpContentType[] getContentTypes() {
		return HttpContentType.values();
	}
}
