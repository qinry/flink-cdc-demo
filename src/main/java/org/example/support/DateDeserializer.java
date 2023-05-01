package org.example.support;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonParser;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonToken;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.DeserializationContext;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {
    private static final Logger log = LoggerFactory.getLogger(DateDeserializer.class);
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            long valueAsLong = jsonParser.getValueAsLong();
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(valueAsLong);
            calendar.add(Calendar.HOUR, -8);
            Date date = calendar.getTime();
            return date;
        }
        String valueAsString = jsonParser.getValueAsString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(valueAsString);
            return date;
        } catch (ParseException e) {
            log.warn("ParseException!", e);
            return null;
        }
    }
}
