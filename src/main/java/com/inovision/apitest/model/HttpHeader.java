package com.inovision.apitest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class HttpHeader extends NameValue {

	private int id;
	private int testCaseCategoryId = -1;
	private int testCaseId = -1;
	
	public HttpHeader() {
		super(null, null);
	}
	
	public HttpHeader(TestCaseCategory tcc, String name, String value) {
		super(name, value);
		testCaseCategoryId = tcc.getId();
	}

	public HttpHeader(TestCase tc, String name, String value) {
		super(name, value);
		testCaseId = tc != null ? tc.getId() : -1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTestCaseCategoryId() {
		return testCaseCategoryId;
	}
	
	public void setTestCaseCategoryId(int testCaseCategoryId) {
		this.testCaseCategoryId = testCaseCategoryId;
	}
	
	public int getTestCaseId() {
		return testCaseId;
	}
	
	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	@Override
	public String toString() {		
		return ToStringBuilder.reflectionToString(this);
	}
}
