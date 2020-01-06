package cz.cas.lib.vzb.card.category;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import core.domain.DomainObject;
import core.util.ApplicationContextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryCollectionSerializer extends StdSerializer<Collection<Category>> {


    protected CategoryCollectionSerializer(Class<Collection<Category>> t) {
        super(t);
    }

    protected CategoryCollectionSerializer(JavaType type) {
        super(type);
    }

    protected CategoryCollectionSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected CategoryCollectionSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Collection<Category> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Map<String, CategoryDto> categoriyDtos = value.stream()
                .collect(Collectors.toMap(DomainObject::getId, CategoryDto::new));
        List<CategoryDto> dtos = new ArrayList<>();
        for (Category cat : value) {
            if (cat.getParent() != null)
                categoriyDtos.get(cat.getParent().getId()).addSubCategory(categoriyDtos.get(cat.getId()));
            else
                dtos.add(categoriyDtos.get(cat.getId()));
        }
        ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
        gen.writeString(objectMapper.writeValueAsString(dtos));
    }
}
