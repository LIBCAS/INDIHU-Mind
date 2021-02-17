package cz.cas.lib.indihumind.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends StdSerializer<Color> {

    public ColorSerializer() {
        super(Color.class);
    }

    @Override
    public void serialize(Color color, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
    }
}
