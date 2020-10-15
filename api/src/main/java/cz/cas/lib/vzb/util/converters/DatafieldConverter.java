package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.util.ApplicationContextUtils;
import cz.cas.lib.vzb.reference.marc.record.Datafield;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatafieldConverter implements AttributeConverter<List<Datafield>, String> {

    @Override
    public String convertToDatabaseColumn(List<Datafield> attribute) {
        if (attribute == null) return null;
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String jsonString = objectMapper.writeValueAsString(attribute);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not convert Datafield[] object to JSON, object:" + attribute);
        }
    }

    @Override
    public List<Datafield> convertToEntityAttribute(String dbData) {
        if (dbData == null) return new ArrayList<>();
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
            Datafield[] fields = objectMapper.readValue(dbData, Datafield[].class);
            return Arrays.asList(fields);
        } catch (IOException e) {
            throw new RuntimeException("could not convert JSON to Datafield[], data:" + dbData);
        }
    }
}
