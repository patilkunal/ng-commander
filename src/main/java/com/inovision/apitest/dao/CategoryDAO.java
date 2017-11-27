package com.inovision.apitest.dao;

import java.util.List;

import com.inovision.apitest.model.TestCaseCategory;

public interface CategoryDAO {

	public List<TestCaseCategory> getTestCategories();
	public TestCaseCategory getTestCategory(int id);
	public void deleteTestCategory(int id);
	public TestCaseCategory saveTestCategory(TestCaseCategory tc);
	public void deleteHeader(int categoryid, int headerid);
}
