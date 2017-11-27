package com.inovision.apitest.model;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.inovision.apitest.util.CustomDateTimeSerializer;

public class TestCaseRun {

	private int id;
	private TestCaseInstance testCaseInstance;
	private Host host;
	private boolean success;
	private Date runDate;
	private int returnCode;
	private String error;
	private String result;
	private HttpContentType contentType;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TestCaseInstance getTestCaseInstance() {
		return testCaseInstance;
	}
	public void setTestCaseInstance(TestCaseInstance testCaseInstance) {
		this.testCaseInstance = testCaseInstance;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	@JsonSerialize(using=CustomDateTimeSerializer.class)
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public void setReturnCode(int code) {
		this.returnCode = code;
	}
	public int getReturnCode() {
		return returnCode;
	}
	public void setContentType(HttpContentType contentType) {
		this.contentType = contentType;
	}
	public HttpContentType getContentType() {
		return contentType;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
