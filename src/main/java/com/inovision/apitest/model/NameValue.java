package com.inovision.apitest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class NameValue {
	
	private String name;
	private String value;
	
	public NameValue(String name, String value) {
		this.name = name;
		this.value = value;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
