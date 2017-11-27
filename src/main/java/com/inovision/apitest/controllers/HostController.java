package com.inovision.apitest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inovision.apitest.dao.HostDAO;
import com.inovision.apitest.dao.ScheduleDAO;
import com.inovision.apitest.dao.TestCaseDAO;
import com.inovision.apitest.model.Host;

@Controller
public class HostController {

	private HostDAO hostDAO;
	private TestCaseDAO testCaseDAO;
	private ScheduleDAO scheduleDAO;
	
	@Autowired
	public void setHostDAO(HostDAO hostDAO) {
		this.hostDAO = hostDAO;
	}
	
	@Autowired
	public void setTestCaseDAO(TestCaseDAO testCaseDAO) {
		this.testCaseDAO = testCaseDAO;
	}
	
	@Autowired
	public void setScheduleDAO(ScheduleDAO scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}
	
	@RequestMapping(produces="application/json", value="/hosts", method=RequestMethod.GET)
	public @ResponseBody List<Host> getHosts() {		
		return hostDAO.getHosts();
	}

	@RequestMapping(produces="application/json", value="/hosts/category/{categoryId}", method=RequestMethod.GET)
	public @ResponseBody List<Host> getHostByCategory(@PathVariable("categoryId") int categoryId) {		
		return hostDAO.getHosts(categoryId);
	}
	
	@RequestMapping(produces="application/json", value="/hosts/{id}", method=RequestMethod.GET)
	public @ResponseBody Host getHost(@PathVariable("id") int id) {		
		return hostDAO.getHost(id);
	}
	
	@RequestMapping(produces="application/json", value="/hosts", method=RequestMethod.POST)
	public @ResponseBody Host saveHost(@RequestBody Host host) {		
		return hostDAO.saveHost(host);
	}	

	@RequestMapping(produces="application/json", value="/hosts/{id}", method=RequestMethod.PUT)
	public @ResponseBody Host updateHost(@RequestBody Host host) {		
		return hostDAO.saveHost(host);
	}	
	
	@RequestMapping(produces="text/plain", value="/hosts/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.ACCEPTED)
	public void deleteHost(@PathVariable("id") int id) {
		testCaseDAO.deleteTestRunByHost(id);
		scheduleDAO.deleteSchedulesByHost(id);
		//in practice we should throw 404 on host not found
		hostDAO.deleteHost(id);
		//return "success";
	}	
	
}
