package com.inovision.apitest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.inovision.apitest.decorator.ValueDecorator;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValueType;

public class TokenParser {

	private static final Logger LOGGER = Logger.getLogger(TokenParser.class);
	private static final List<String> reservedKeywords = new ArrayList<String>(1);
	private static final String BLANK = "";
	private static final Pattern pattern = Pattern.compile("\\{\\s*?(\\w+)\\s*?\\}", Pattern.DOTALL + Pattern.MULTILINE);
	
	static {
		for(ValueDecorator vd : ValueDecorator.values()) {
			reservedKeywords.add(vd.name());
		}		
	}
	/**
	 * Parse URL and Data and get the placeholders
	 * @param url
	 * @param data
	 * @return
	 */
	public static List<TestCaseValue> parseTokens(String url, String data) {
		String urlParts[] = url.split("\\?");
		String path = urlParts[0];
		String querystring = urlParts.length > 1 ? urlParts[1] : null;
		List<TestCaseValue> list = new ArrayList<TestCaseValue>(5);
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String token = null;
		while(tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			TestCaseValue tv = getTestCaseValue(token);
			if(tv != null) {
				tv.setValueType(ValueType.PATHPARAM);
				list.add(tv);
			}
		}
		
		//do we have query string at end?
		if(querystring != null && querystring.length() > 0) {
			tokenizer = new StringTokenizer(querystring, "&");
			while(tokenizer.hasMoreTokens()) {
				String queryparams = tokenizer.nextToken();
				TestCaseValue tv = null;
				if(queryparams.startsWith("{")) {
					//A special case where queryparam is value itself (it is required then)
					tv = getTestCaseValue(queryparams);
					tv.setValueType(ValueType.QUERYPARAM);
					tv.setRequired(true);
				} else if(queryparams.contains("=")) {		
					String[] nvpair = queryparams.split("=");
					//For Query parameters we read name parameters only
					String value = (nvpair.length > 1 && !StringUtils.isEmpty(nvpair[1]))  ? nvpair[1] : BLANK;
					tv =  new TestCaseValue(nvpair[0], value); //getTestCaseValue(nv);
					tv.setValueType(ValueType.QUERYPARAM);
					list.add(tv);
				}  
				//If no equal sign, then it is hardcoded query param and no value needed (no test case value)
					
			}
		}
		
		if(data != null && data.length() > 0) {
			Matcher m = pattern.matcher(data);
			while(m.find()) {
				String name = m.group();
				TestCaseValue tv = new TestCaseValue();
				tv.setName(name.substring(1, name.length() - 1));
				tv.setValueType(ValueType.DATAVARIABLE);
				list.add(tv);
			}
		}
		return list;
	}
	
	private static TestCaseValue getTestCaseValue(String name) {
		TestCaseValue tv = null;
		if(name != null) { 
			if(name.startsWith("{") && name.endsWith("}")) {
				//if placeholder starts with '?' then it is optional. TODO: do we put blank value? 
				String str = name.charAt(1) == '?' ? name.substring(2, name.length() - 1) : name.substring(1, name.length() - 1);
				tv = new TestCaseValue();
				tv.setName(str);
				if(name.charAt(1) == '?') {
					tv.setRequired(false);
				} else {
					tv.setRequired(true);				
				}
			}
		}
		return tv;
	}
	
}
