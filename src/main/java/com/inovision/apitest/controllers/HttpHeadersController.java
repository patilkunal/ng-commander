package com.inovision.apitest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.inovision.apitest.dao.HttpHeadersDAO;

@Controller
@RequestMapping("/headers")
public class HttpHeadersController {

	private HttpHeadersDAO httpHeadersDAO;
	
	@Autowired
	public void setHttpHeadersDAO(HttpHeadersDAO httpHeadersDAO) {
		this.httpHeadersDAO = httpHeadersDAO;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	@ResponseStatus(value=HttpStatus.ACCEPTED)
	public void deleteHeader(@PathVariable("id") int id) {
		httpHeadersDAO.deleteHttpHeader(id);
	}
	
}
