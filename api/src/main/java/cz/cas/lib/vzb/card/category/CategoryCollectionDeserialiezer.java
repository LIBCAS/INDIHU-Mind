package cz.cas.lib.vzb.card.category;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import core.util.ApplicationContextUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CategoryCollectionDeserialiezer extends StdDeserializer<Collection<Category>> {
    public CategoryCollectionDeserialiezer(Class<?> vc) {
        super(vc);
    }

    public CategoryCollectionDeserialiezer(JavaType valueType) {
        super(valueType);
    }

    public CategoryCollectionDeserialiezer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public Collection<Category> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
        CategoryDto[] categoryDtos = objectMapper.readValue(p, CategoryDto[].class);
        List<Category> categories = new ArrayList<>();
        for (CategoryDto categoryDto : categoryDtos) {
            flatten(categoryDto, categories);
        }
        return categories;
    }

    private void flatten(CategoryDto dto, List<Category> categories) {
        Category c = new Category();
        c.setOrdinalNumber(dto.getOrdinalNumber());
        c.setName(dto.getName());
        if (dto.getParentId() != null) {
            Category parent = new Category();
            parent.setId(dto.getParentId());
            c.setParent(parent);
        }
        if (CollectionUtils.isEmpty(dto.getSubCategories())) {
            categories.add(c);
            return;
        }
        for (CategoryDto subCategory : dto.getSubCategories()) {
            flatten(subCategory, categories);
        }
    }
}
