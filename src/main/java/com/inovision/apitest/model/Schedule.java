package com.inovision.apitest.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Schedule {

	private int id = -1;
	private String name;
	private String cronExpression;
	private boolean active;
	private List<TestCaseInstance> testCaseInstances;
	private int hostId;
	private int categoryId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<TestCaseInstance> getTestCaseInstances() {
		return testCaseInstances;
	}
	
	public void setTestCaseInstances(List<TestCaseInstance> testCaseInstances) {
		this.testCaseInstances = testCaseInstances;
	}
	
	public int getHostId() {
		return hostId;
	}
	
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) ? this.id == ((Schedule)obj).id : false;
	}
	
	@Override
	public int hashCode() {
		return 37 * id;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
