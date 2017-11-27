package com.inovision.apitest.model;

import java.util.List;

import org.springframework.http.HttpMethod;

public class TestCase {
	
	private int id;
	private int testCategoryId;
	private String name;
	private String description;
	private String restUrl;	
	private HttpMethod method = HttpMethod.GET;
	private String data;
	private HttpContentType responseType;
	private boolean validateOutput;
	private boolean allowBlankOutput;
	private ValidationType validateType;
	private String outputTemplate;
	private List<HttpHeader> httpHeaders;
	
	private int instanceCount;
	
	public TestCase() {
		this(-1);
	}
	
	public TestCase(int id) {
		this.validateType = ValidationType.JSON;
		this.responseType = HttpContentType.JSON;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}	
	public void setId(int id) {
		this.id = id;
	}
	public String getRestUrl() {
		return restUrl;
	}
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}
	public int getTestCategoryId() {
		return testCategoryId;
	}
	public void setTestCategoryId(int testCategoryId) {
		this.testCategoryId = testCategoryId;
	}
	public HttpMethod getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
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
	public int getInstanceCount() {
		return instanceCount;
	}
	public void setInstanceCount(int instances) {
		this.instanceCount = instances;
	}
	public void setResponseType(HttpContentType responseType) {
		this.responseType = responseType;
	}
	public HttpContentType getResponseType() {
		return responseType;
	}
	public boolean isValidateOutput() {
		return validateOutput;
	}
	public void setValidateOutput(boolean validateOutput) {
		this.validateOutput = validateOutput;
	}
	public boolean isAllowBlankOutput() {
		return allowBlankOutput;
	}
	public void setAllowBlankOutput(boolean allowBlankOutput) {
		this.allowBlankOutput = allowBlankOutput;
	}
	public ValidationType getValidateType() {
		return validateType;
	}
	public void setValidateType(ValidationType validateType) {
		this.validateType = validateType;
	}
	public String getOutputTemplate() {
		return outputTemplate;
	}
	public void setOutputTemplate(String outputTemplate) {
		this.outputTemplate = outputTemplate;
	}
	public List<HttpHeader> getHttpHeaders() {
		return httpHeaders;
	}
	public void setHttpHeaders(List<HttpHeader> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof TestCase) && (this.id == ((TestCase)obj).id);
	}
	
	@Override
	public int hashCode() {
		return this.id * 37;
	}
	
	@Override
	public String toString() {
		return String.format("TestCase [name: %1$s] [RestURL: %2$s] [Data: %3$s] [Method: %4$s]]", name, restUrl, data, method.name());
	}
}
