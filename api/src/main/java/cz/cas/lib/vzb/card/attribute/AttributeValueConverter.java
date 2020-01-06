package cz.cas.lib.vzb.card.attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.util.ApplicationContextUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AttributeValueConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object value) {
        try {
            ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not convert object to JSON, object:" + value);
        }
    }

    /**
     * simply return json string and handle the rest in postload
     *
     * @param value
     * @return
     */
    @Override
    public Object convertToEntityAttribute(String value) {
        return value;
    }

}