package com.inovision.apitest.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum HttpContentType {

	TEXT("text/plain"),
	JSON("application/json"),
	XML("application/xml"),
	HTML("text/html");
	
	private String type;
	
	private HttpContentType(String type) {
		this.type = type;
	}

	@JsonValue
	public String getType() {
		return type;
	}
	
	@JsonCreator
	public static HttpContentType fromType(String type) {
		for(HttpContentType contenttype : values()) {
			if(contenttype.getType().equalsIgnoreCase(type)) {
				return contenttype;
			}
		}
		return null;
	}
}
