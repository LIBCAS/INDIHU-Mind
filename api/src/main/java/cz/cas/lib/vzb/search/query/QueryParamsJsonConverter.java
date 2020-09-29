package cz.cas.lib.vzb.search.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.util.ApplicationContextUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.UncheckedIOException;

@Converter
public class QueryParamsJsonConverter implements AttributeConverter<QueryParams, String> {

    @Override
    public String convertToDatabaseColumn(QueryParams attribute) {
        if (attribute == null)
            return null;
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getApplicationContext().getBean(ObjectMapper.class);
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert object to JSON, object:" + attribute);
        }
    }

    @Override
    public QueryParams convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getApplicationContext().getBean(ObjectMapper.class);
            return objectMapper.readValue(dbData, QueryParams.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to parse JSON from DB", e);
        }
    }

}
