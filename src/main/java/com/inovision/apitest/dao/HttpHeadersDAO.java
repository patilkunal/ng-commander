package com.inovision.apitest.dao;

import java.util.List;

import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestCaseCategory;

public interface HttpHeadersDAO {

	public List<HttpHeader> saveHttpHeader(TestCaseCategory tcc, List<HttpHeader> header);
	public List<HttpHeader> saveHttpHeader(TestCase tc, List<HttpHeader> header);
	public List<HttpHeader> getHeaders(TestCase tc);
	public List<HttpHeader> getHeaders(TestCaseCategory tcc);
	public void deleteHttpHeader(int id);
	
}
