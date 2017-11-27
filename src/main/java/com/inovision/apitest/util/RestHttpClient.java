package com.inovision.apitest.util;

import java.util.List;

import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestResult;

public interface RestHttpClient {

	public abstract void shutdown();

	public abstract TestResult doHttpRequest(TestCase testCase, Host host, List<HttpHeader> headers)
			throws Exception;

}