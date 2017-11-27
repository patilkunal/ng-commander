package com.inovision.apitest.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexResultComparator implements ResultComparator {
	
	private Map<String, Pattern> patternCache = new HashMap<String, Pattern>();
	private String regex;

	@Override
	public boolean compare(String regex, String rhsJson) {
		this.regex = regex;
		Pattern regexpattern = null;
		synchronized (patternCache) {
			regexpattern = patternCache.get(regex);
			if(regexpattern == null) {
				regexpattern = Pattern.compile(regex);
				patternCache.put(regex, regexpattern);
			} 
		}
		Matcher matcher = regexpattern.matcher(rhsJson);
		return matcher.matches();
	}

	@Override
	public List<String> getErrors() {
		return Arrays.asList("The ouput response does not match regular expression : " + regex);
	}

	@Override
	public List<String> getWarnings() {
		return null;
	}

}
