package com.inovision.apitest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestCaseInstance {

	private int id = -1;
	private int testCaseId;
	private int userId;
	private String name;
	private String description;
	
	private List<TestCaseValue> testCaseValues;
	private List<HttpHeader> httpHeaders;
	
	private boolean validateOutput;
	private boolean allowBlankOutput;
	private ValidationType validateType;
	private String outputTemplate;

	public TestCaseInstance() {
		this.validateType = ValidationType.JSON;
	};
	
	public TestCaseInstance(int id) {
		this.id = id;
	}
	public TestCaseInstance(int id, int testCaseId, String name, String desc) {
		this.id = id;
		this.testCaseId = testCaseId;
		this.name = name;
		this.description = desc;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
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

	public List<TestCaseValue> getTestCaseValues() {
		return testCaseValues;
	}

	public void setTestCaseValues(List<TestCaseValue> testCaseValues) {
		this.testCaseValues = testCaseValues;
	}
	
	public String getOutputTemplate() {
		return outputTemplate;
	}
	
	public void setOutputTemplate(String outputTemplate) {
		this.outputTemplate = outputTemplate;
	}
	
	public void setValidateOutput(boolean validateOutput) {
		this.validateOutput = validateOutput;
	}
	
	public boolean isValidateOutput() {
		return validateOutput;
	}
	
	public void setAllowBlankOutput(boolean allowBlankOutput) {
		this.allowBlankOutput = allowBlankOutput;
	}
	
	public boolean isAllowBlankOutput() {
		return allowBlankOutput;
	}
	
	public void setValidateType(ValidationType validateType) {
		this.validateType = validateType;
	}
	
	public ValidationType getValidateType() {
		return validateType;
	}
	
	public List<HttpHeader> getHttpHeaders() {
		return httpHeaders;
	}
	public void setHttpHeaders(List<HttpHeader> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	public void appendHttpHeaders(List<HttpHeader> httpHeaders) {
		if(this.httpHeaders != null) {
			this.httpHeaders.addAll(httpHeaders);
		} else {
			this.httpHeaders = httpHeaders;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof TestCaseInstance) && 
				(((TestCaseInstance)obj).id == this.id);
	}
	
	@Override
	public int hashCode() {
		return 37 * id;
	}
	
	@Override
	public String toString() {		
		return ToStringBuilder.reflectionToString(this);
	}
	
}
