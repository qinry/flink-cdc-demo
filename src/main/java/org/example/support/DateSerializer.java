package org.example.support;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonGenerator;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonSerializer;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = df.format(date);
        jsonGenerator.writeObject(dateStr);
    }
}
