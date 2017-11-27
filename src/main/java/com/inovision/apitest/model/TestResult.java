package com.inovision.apitest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestResult {

	private int testInstanceId;
	private int hostId;
	private boolean success;
	private int returnCode;
	private String error;
	private String result;
	private HttpContentType contentType;

	public int getTestInstanceId() {
		return testInstanceId;
	}
	public void setTestInstanceId(int testInstanceId) {
		this.testInstanceId = testInstanceId;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
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
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
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
