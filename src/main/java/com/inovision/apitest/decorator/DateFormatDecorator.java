package com.inovision.apitest.decorator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatDecorator implements Decorator {

	private String format;
	
	public DateFormatDecorator(String format) {
		this.format = format;
	}
	
	public String getValue() {
		SimpleDateFormat df = new SimpleDateFormat(format);		
		return df.format(new Date());
	}
}
