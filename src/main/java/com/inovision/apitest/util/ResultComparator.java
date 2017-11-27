package com.inovision.apitest.util;

import java.util.List;

public interface ResultComparator {

	public boolean compare(String lhsJson, String rhsJson);
	public List<String> getErrors();
	public List<String> getWarnings();
}
