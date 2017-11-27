package com.inovision.apitest.util;

import java.util.List;

import com.inovision.apitest.model.TestCaseValue;
import com.inovision.apitest.model.ValueType;

public class TestTokenParser extends junit.framework.TestCase {

	public void testTokenParser1() {
		String url = "/test/{name}/search?id=&foo=";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
		assertEquals("id", list.get(1).getName());
		assertEquals(ValueType.QUERYPARAM, list.get(1).getValueType());		
		assertEquals("foo", list.get(2).getName());
		assertEquals(ValueType.QUERYPARAM, list.get(2).getValueType());		
	}

	public void testTokenParser2() {
		String url = "/test/{name}/search";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
	}

	public void testTokenParser3() {
		String url = "/test/{name}/search?id=";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
		assertEquals("id", list.get(1).getName());
		assertEquals(ValueType.QUERYPARAM, list.get(1).getValueType());		
	}

	public void testTokenParser3a() {
		String url = "/test/{name}/search?id";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
	}
	
	public void testTokenParser4() {
		String url = "/test/{name}/search?";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
	}

	public void testTokenParser5() {
		String url = "/test/{name}/search?id=someid&foo=somefooo";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
		assertEquals("id", list.get(1).getName());
		assertEquals("someid", list.get(1).getValue());
		assertEquals(ValueType.QUERYPARAM, list.get(1).getValueType());		
		assertEquals("foo", list.get(2).getName());
		assertEquals("somefooo", list.get(2).getValue());
		assertEquals(ValueType.QUERYPARAM, list.get(2).getValueType());		
	}	

	public void testTokenParser6() {
		String url = "/coxsl/guide/2.0/search/integrated/controllers/{controller}/lineups/{lineup}/events/?searchString=&max=100&duplicatePreference=hd&stations=ip,noadult&deviceType=Android_TestCommander&vodPkgs=&siteId=";
		List<TestCaseValue> list = TokenParser.parseTokens(url, null);
		assertNotNull(list);
		assertEquals(9, list.size());
		assertEquals("controller", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
		assertEquals("lineup", list.get(1).getName());
		assertEquals(ValueType.PATHPARAM, list.get(1).getValueType());		
		assertEquals("searchString", list.get(2).getName());
		assertEquals("", list.get(2).getValue());
		assertEquals(ValueType.QUERYPARAM, list.get(2).getValueType());		
		assertEquals("max", list.get(3).getName());
		assertEquals("100", list.get(3).getValue());
		assertEquals(ValueType.QUERYPARAM, list.get(3).getValueType());		
	}

	public void testTokenParser7() {
		String url = "/coxsl/guide/2.0/search/{deviceID}?saToken=123";
		List<TestCaseValue> list = TokenParser.parseTokens(url, "{\"channelNumber\":\"{channelNumber}\",\"startBuffer\":1,\"endBuffer\":2,\"airingStartTime\":\"{airStartTime}\"}");
		assertNotNull(list);
		assertEquals(4, list.size());
		assertEquals("deviceID", list.get(0).getName());
		assertEquals(ValueType.PATHPARAM, list.get(0).getValueType());		
		assertNull(list.get(0).getValue());
		assertEquals("saToken", list.get(1).getName());
		assertEquals(ValueType.QUERYPARAM, list.get(1).getValueType());		
		assertTrue("123".equals(list.get(1).getValue()));

		assertEquals("channelNumber", list.get(2).getName());
		assertEquals(ValueType.DATAVARIABLE, list.get(2).getValueType());		
		
	}
}
