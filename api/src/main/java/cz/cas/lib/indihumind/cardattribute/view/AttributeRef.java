package cz.cas.lib.indihumind.cardattribute.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import core.domain.DomainObject;
import core.util.ApplicationContextUtils;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.util.converters.AttributeValueDeserializer;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.IOException;

/**
 * @see Attribute
 */
@Immutable
@Getter
@Entity(name = "attribute_ref")
@Table(name = "vzb_attribute")
@JsonPropertyOrder({"id", "name", "type", "ordinalNumber", "value"})
public class AttributeRef extends DomainObject implements EntityProjection<Attribute> {

    private int ordinalNumber;

    private String name;

    @Enumerated(EnumType.STRING)
    private AttributeType type;

    @Nullable
    @JsonDeserialize(using = AttributeValueDeserializer.class)
    @Transient
    private Object value;

    @Nullable
    @JsonIgnore
    private String jsonValue;

    /**
     * Workaround because {@link AttributeConverter} can not access {@link #getType()}
     * and therefore object mapper does not know the mapping class.
     */
    @PostLoad
    @PostUpdate
    @PostPersist
    private void setValue() throws IOException {
        ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
        value = objectMapper.readValue(jsonValue, type.getValueClass());
    }

    @Override
    public Attribute toEntity() {
        Attribute entity = new Attribute();
        entity.setId(id);
        entity.setName(name);
        entity.setOrdinalNumber(ordinalNumber);
        entity.setType(type);
        entity.setValue(value);
        entity.setJsonValue(jsonValue);
        return entity;
    }
}
