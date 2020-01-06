package cz.cas.lib.vzb.card.label;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {

    public ColorDeserializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String colorString = p.getValueAsString();
        return new Color(
                Integer.valueOf(colorString.substring(1, 3), 16),
                Integer.valueOf(colorString.substring(3, 5), 16),
                Integer.valueOf(colorString.substring(5, 7), 16));
    }
}
