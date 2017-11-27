package com.inovision.apitest.dao;

import java.util.List;

import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseRunFilter;
import com.inovision.apitest.model.TestCaseValue;

public interface TestCaseDAO {

	public List<TestCaseInstance> getTestCaseInstances(int categoryId);
	public List<TestCaseInstance> getTestCaseInstances(TestCase tc);
	public TestCaseInstance getTestCaseInstance(int id);
	public TestCaseInstance saveTestCaseInstance(TestCaseInstance ti);
	public void deleteTestCaseInstance(int id);

	public List<TestCase> getTestCases(int categoryId);
	public TestCase getTestCase(int id);
	public TestCase saveTestCase(TestCase testCase);
	public void deleteTestCase(int id);


	public List<TestCaseValue> getTestCaseValues(int testCaseInstanceId);
	public void deleteTestCaseValues(TestCaseInstance testInstance);
	
	public List<TestCaseRun> getTestCaseRuns(TestCaseRunFilter filter);
	public TestCaseRun saveTestRun(TestCaseRun run);
	public void updateTestRun(TestCaseRun run);
	public List<TestCaseRun> getTestRunList();
	public TestCaseRun getTestRun(int id);
	public List<TestCaseRun> getTestRunList(TestCaseCategory category);
	public void deleteTestRun(TestCaseInstance testInstance);
	public void deleteTestRunByHost(int hostid);
	
}
