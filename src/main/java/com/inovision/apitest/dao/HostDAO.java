package com.inovision.apitest.dao;

import java.util.List;

import com.inovision.apitest.model.Host;

public interface HostDAO {
	
	public List<Host> getHosts();
	public List<Host> getHosts(int testCategoryId);
	public Host getHost(int id);
	public Host saveHost(Host host);
	public void deleteHost(int id);
	
}
