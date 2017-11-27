package com.inovision.apitest.util;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomDateTimeSerializer extends JsonSerializer<Date> {

	ThreadLocalDateTimeFormat df = new ThreadLocalDateTimeFormat();

	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		
        String formattedDate = null;
        if(date != null) {
        	formattedDate = df.convertToString(date);
        }
        gen.writeString(formattedDate);
		
	}


}
