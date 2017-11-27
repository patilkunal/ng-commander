package com.inovision.apitest.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inovision.apitest.dao.CategoryDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.manager.TestRunManager;
import com.inovision.apitest.model.Statistics;
import com.inovision.apitest.model.StatisticsList;
import com.inovision.apitest.model.TestCaseCategory;
import com.inovision.apitest.model.TestCaseInstance;
import com.inovision.apitest.model.TestCaseRun;
import com.inovision.apitest.model.TestCaseRunFilter;

@Controller
@RequestMapping(value="/stats")
public class StatsController {

	private TestCaseDAO testCaseDAO;	
	private TestRunManager testRunManager;
	private CategoryDAO categoryDAO;
	
	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}
	
	@Autowired
	public void setTestRunManager(TestRunManager testRunManager) {
		this.testRunManager = testRunManager;
	}
	
	@Autowired
	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}
	
	@RequestMapping(value="/categories", method=RequestMethod.GET)
	public @ResponseBody ArrayList<Statistics> getCategoriesStats() {
		ArrayList<Statistics> stats = new ArrayList<Statistics>();
		List<TestCaseCategory> cats = categoryDAO.getTestCategories();
		TestCaseRunFilter filter = new TestCaseRunFilter();
		for(TestCaseCategory c : cats) {
			filter.setTestCaseCategoryId(c.getId());
			List<TestCaseRun> runs = testCaseDAO.getTestCaseRuns(filter);
			int success = 0;
			int failures = 0;
			for(TestCaseRun r : runs) {
				//if((r.getRunDate() != null) && (r.getOutput() != null)) {
					if(r.isSuccess()) {
						success++;
					} else {
						failures++;
					}
				//}
			}
			stats.add(new Statistics(c.getName(), c.getName(), success, failures));
		}
		
		return stats;
	}
	
	@RequestMapping(value="/tests/category/{categoryId}", method=RequestMethod.GET)
	public @ResponseBody List<Statistics> getTestByCategoryStats(@PathVariable("categoryId") int categoryId) {
		List<Statistics> stats = new ArrayList<Statistics>();
		
		TestCaseCategory cat = categoryDAO.getTestCategory(categoryId);
		
		TestCaseRunFilter filter = new TestCaseRunFilter();
		filter.setTestCaseCategoryId(categoryId);
		List<TestCaseRun> runs = testCaseDAO.getTestCaseRuns(filter);
		for(TestCaseRun r : runs) {
			TestCaseInstance ti = r.getTestCaseInstance();
			Statistics s = new Statistics(cat.getName(), ti.getName(), 0, 0);
			Statistics stat = null;
			if(!stats.contains(s)) {
				stats.add(s);
				stat = s;
			} else {
				stat = stats.get(stats.indexOf(s));
			}
			//with outer join we may not have a run, so check for output and rundate for null values
			if((r.getRunDate() != null) && ((r.getError() != null) || (r.getResult() != null))) {
				if(r.isSuccess()) {
					stat.setSuccess(stat.getSuccess()+1);
				} else {
					stat.setFailures(stat.getFailures()+1);
				}
			}
		}
		
		
		return stats;
	}

	@RequestMapping(value="/tests/{testId}", method=RequestMethod.GET)
	public void getTestStats() {
		
	}
	
}
