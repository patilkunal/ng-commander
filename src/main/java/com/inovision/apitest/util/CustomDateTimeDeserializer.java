package com.inovision.apitest.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class CustomDateTimeDeserializer extends JsonDeserializer<Date>{

	ThreadLocalDateTimeFormat df = new ThreadLocalDateTimeFormat();
	
	@Override
	public Date deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		Date date = null;
		try {
			String text = parser.getText();
			if((text != null) && (text.length() > 0)) {
				date = df.convertToDate(parser.getText());
			}
		} catch(ParseException pe) {
			throw new IOException(pe.getMessage());
		}
		
		return date;
	}
}
