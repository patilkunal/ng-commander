package com.inovision.apitest.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inovision.apitest.dao.CategoryDAO;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCaseCategory;

@Controller
@RequestMapping("/categories")
public class TestCategoryController {
	
	private static final Logger LOGGER = Logger.getLogger(TestCategoryController.class);

	private CategoryDAO categoryDAO;

	@Autowired
	public void setTestCaseDAO(CategoryDAO testCaseDAO) {
		this.categoryDAO = testCaseDAO;
	}

	@RequestMapping(produces="application/json", method=RequestMethod.GET)
	public @ResponseBody List<TestCaseCategory> getTestCaseCategories() {		
		return categoryDAO.getTestCategories();
	}

	@RequestMapping(produces="application/json", value="/{id}", method=RequestMethod.GET)
	public @ResponseBody TestCaseCategory getTestCaseCategory(@PathVariable("id") int id) {		
		return categoryDAO.getTestCategory(id);
	}

	@RequestMapping(produces="application/json", method=RequestMethod.POST)
	public @ResponseBody TestCaseCategory saveTestCaseCategory(@RequestBody TestCaseCategory tc) {
		LOGGER.debug("Saving category: " + tc);
		return categoryDAO.saveTestCategory(tc);
	}

	@RequestMapping(produces="application/json", value="/{id}", method=RequestMethod.PUT)
	public @ResponseBody TestCaseCategory updateTestCaseCategory(@RequestBody TestCaseCategory tc) {		
		return categoryDAO.saveTestCategory(tc);
	}
	
	@RequestMapping(produces="application/json", value="/{id}/headers", method=RequestMethod.GET)
	public @ResponseBody List<HttpHeader> getTestCategoryHttpHeaders(@PathVariable("id") int id) {
		TestCaseCategory tcc = categoryDAO.getTestCategory(id);
		return tcc.getHeaders();
	}
	
	@RequestMapping(value="/{id}/headers/{headerid}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.ACCEPTED)
	public void deleteCategoryHttpHeaders(@PathVariable("id") int id, @PathVariable("headerid") int headerid) {
		LOGGER.debug("Deleting category header");
		categoryDAO.deleteHeader(id, headerid);
	}
	
}
