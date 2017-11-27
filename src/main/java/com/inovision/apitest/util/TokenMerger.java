/**
 * 
 */
package com.inovision.apitest.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.inovision.apitest.decorator.ValueDecorator;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValueType;

/**
 * @author kunpatil
 *
 */
public class TokenMerger {

	private static final Logger LOGGER = Logger.getLogger(TokenMerger.class);
	private static final String BLANK = "";
	/**
	 * Merges user supplied values from Test Case Instance to a given Test Case
	 * and returns new TestCase object with all the tokens replaced with values
	 * 
	 * @param tc TestCase
	 * @param tci TestCaseInstance
	 * @return TestCase with tokens replaced by values
	 */
	public static TestCase mergeTokenValues(TestCase tc, TestCaseInstance tci) {
		TestCase newtc = new TestCase();
		BeanUtils.copyProperties(tc, newtc);
		String origUrl = new String(tc.getRestUrl());
		String testdata = tc.getData();
		boolean hasData = testdata != null && testdata.length() > 0;
		List<TestCaseValue> values = tci.getTestCaseValues();
		if(values != null) {
			for(TestCaseValue tcv : values) {
				String value = StringUtils.isEmpty(tcv.getValue()) ? BLANK : tcv.getValue();
				if(tcv.getValueType() == ValueType.PATHPARAM) {
					String searchname = String.format("\\{%s\\}", tcv.getName());
					origUrl = origUrl.replaceAll(searchname, value);
					if(hasData) {
						testdata = testdata.replaceAll(searchname, value);
					}
				} else if(tcv.getValueType() == ValueType.QUERYPARAM) {
					//QUERY value type do not apply to Test Data
					String searchname = String.format("%s=", tcv.getName());
					try {
						value = URLEncoder.encode(value, "UTF-8");
					} catch(UnsupportedEncodingException uee) { LOGGER.error("Unable to encode query param", uee); }
					origUrl = origUrl.replaceAll(searchname, searchname + value);
				}
			}
		}
		//Replace all VALUE placeholders like NOW, SATOKEN, etc.
		for(ValueDecorator vd : ValueDecorator.values()) {
			String searchname = String.format("#%s#", vd.name());
			String vd_value = vd.getValue();
			origUrl = origUrl.replaceAll(searchname, vd_value);
			if(hasData) {
				testdata = testdata.replaceAll(searchname, vd_value);
			}
		}
		
		newtc.setRestUrl(origUrl);
		newtc.setData(testdata);
		
		return newtc;
	}
	
	
}
