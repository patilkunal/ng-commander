package com.inovision.apitest.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValueType;

public class TestTokenMerger extends junit.framework.TestCase {

	public void testTokenMerger1() {
		TestCase tc = new TestCase();
		tc.setRestUrl("/test/{name}/search?id=&foo=");
		tc.setData("{'data' : #NOW# }");
		
		TestCaseInstance tci = new TestCaseInstance();
		List <TestCaseValue> testCaseValues = new ArrayList<TestCaseValue>();
		tci.setTestCaseValues(testCaseValues);
		testCaseValues.add(new TestCaseValue("name", "john", ValueType.PATHPARAM));
		testCaseValues.add(new TestCaseValue("id", "100", ValueType.QUERYPARAM));
		testCaseValues.add(new TestCaseValue("foo", "foovalue", ValueType.QUERYPARAM));
		
		TestCase retval = TokenMerger.mergeTokenValues(tc, tci);
		assertNotNull(retval);
		assertEquals("/test/john/search?id=100&foo=foovalue", retval.getRestUrl());
		assertNotNull(retval.getData());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String now = df.format(new Date());
		assertTrue(retval.getData().contains(now));
	}

	public void testTokenMerger2() {
		TestCase tc = new TestCase();
		tc.setRestUrl("/test/{name}/search?id=&foo=");
		
		TestCaseInstance tci = new TestCaseInstance();
		List <TestCaseValue> testCaseValues = new ArrayList<TestCaseValue>();
		tci.setTestCaseValues(testCaseValues);
		testCaseValues.add(new TestCaseValue("name", "john", ValueType.PATHPARAM));
		testCaseValues.add(new TestCaseValue("id", "100", ValueType.QUERYPARAM));
		
		TestCase retval = TokenMerger.mergeTokenValues(tc, tci);
		assertNotNull(retval);
		assertEquals("/test/john/search?id=100&foo=", retval.getRestUrl());
	}

	public void testTokenMerger3() {
		TestCase tc = new TestCase();
		tc.setRestUrl("/test/{name}/search?id=&foo=");
		
		TestCaseInstance tci = new TestCaseInstance();
		List <TestCaseValue> testCaseValues = new ArrayList<TestCaseValue>();
		tci.setTestCaseValues(testCaseValues);
		testCaseValues.add(new TestCaseValue("name", "john", ValueType.PATHPARAM));
		testCaseValues.add(new TestCaseValue("foo", "foovalue", ValueType.QUERYPARAM));
		
		TestCase retval = TokenMerger.mergeTokenValues(tc, tci);
		assertNotNull(retval);
		assertEquals("/test/john/search?id=&foo=foovalue", retval.getRestUrl());
	}
}
