package com.inovision.apitest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestCaseCategory {

	private int id;
	private String name;
	private String description;
	private List<HttpHeader> headers;
	private int hostCount;
	private int testCount;
	
	public TestCaseCategory() {
	}
	
	public TestCaseCategory(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<HttpHeader> getHeaders() {
		return headers;
	}
	public void setHeaders(List<HttpHeader> headers) {
		this.headers = headers;
	}
	public int getHostCount() {
		return hostCount;
	}
	public void setHostCount(int hostCount) {
		this.hostCount = hostCount;
	}
	public int getTestCount() {
		return testCount;
	}
	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
