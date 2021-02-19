package cz.cas.lib.indihumind.util.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.util.ApplicationContextUtils;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TemplateFieldsConverter implements AttributeConverter<List<TemplateField>, String> {

    @Override
    public String convertToDatabaseColumn(List<TemplateField> attribute) {
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String jsonString = objectMapper.writeValueAsString(attribute);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not convert object to JSON, object:" + attribute);
        }
    }

    @Override
    public List<TemplateField> convertToEntityAttribute(String dbData) {
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
            TemplateField[] fields = objectMapper.readValue(dbData, TemplateField[].class);
            return Arrays.asList(fields);
        } catch (IOException e) {
            throw new RuntimeException("could not convert JSON to TemplateField[], data:" + dbData);
        }
    }

}
