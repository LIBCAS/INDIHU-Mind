package cz.cas.lib.indihumind.util.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.awt.*;

@Converter
public class ColorConverter implements AttributeConverter<Color, String> {

    /**
     * Convert Color object to a String with hex format
     */
    @Override
    public String convertToDatabaseColumn(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Convert a String with hex format to a Color object
     */
    @Override
    public Color convertToEntityAttribute(String colorString) {
        return new Color(
                Integer.valueOf(colorString.substring(1, 3), 16),
                Integer.valueOf(colorString.substring(3, 5), 16),
                Integer.valueOf(colorString.substring(5, 7), 16));
    }

}