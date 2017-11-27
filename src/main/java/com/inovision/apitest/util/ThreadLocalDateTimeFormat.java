package com.inovision.apitest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadLocalDateTimeFormat {
	
	private ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		
	};

	public String convertToString(Date date) {
		return df.get().format(date);
	}
	
	public Date convertToDate(String str) throws ParseException {
		return df.get().parse(str);
	}
}
