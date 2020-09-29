package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cas.lib.vzb.card.attribute.Attribute;

import java.io.IOException;

public class AttributeValueDeserializer extends StdDeserializer<Object> {

    private static final long serialVersionUID = 1L;

    protected AttributeValueDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object attributeObj = jp.getParsingContext().getCurrentValue();
        if (attributeObj == null)
            attributeObj = jp.getParsingContext().getParent().getCurrentValue();
        Attribute attr = (Attribute) attributeObj;
        return jp.readValueAs(attr.getType().getValueClass());
    }


}
