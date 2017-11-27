package com.inovision.apitest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestCaseValue {

	private int id;
	private int testCaseInstanceId = -1;
	private String name;
	private String value;
	private ValueType valueType;
	private boolean required;
	
	public TestCaseValue() {
	}
	
	public TestCaseValue(String name, String value) {
		this(name, value, value != null && value.length() > 0 ? ValueType.PATHPARAM : ValueType.QUERYPARAM);
	}
	
	public TestCaseValue(String name, String value, ValueType vtype) {
		this.name = name;
		this.value = value;
		this.valueType = vtype;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTestCaseInstanceId() {
		return testCaseInstanceId;
	}
	public void setTestCaseInstanceId(int testCaseInstanceId) {
		this.testCaseInstanceId = testCaseInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public ValueType getValueType() {
		return valueType;
	}
	public void setValueType(ValueType valueType) {		
		this.valueType = valueType;
		setRequired((valueType != null && ValueType.PATHPARAM == valueType));
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
