package com.inovision.apitest.util;

import com.inovision.apitest.model.Parameter;

public interface CronFormatter {

	public String format(String cronExpression, Parameter[] params);
	public Parameter[] getParameters();
}
