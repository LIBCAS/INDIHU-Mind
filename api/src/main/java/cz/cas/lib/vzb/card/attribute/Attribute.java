package cz.cas.lib.vzb.card.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import core.domain.DomainObject;
import core.util.ApplicationContextUtils;
import cz.cas.lib.vzb.card.CardContent;
import cz.cas.lib.vzb.util.converters.AttributeValueDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vzb_attribute")
public class Attribute extends DomainObject {

    @ManyToOne
    @JsonIgnore
    private CardContent cardContent;

    private int ordinalNumber;

    private String name;

    @Enumerated(EnumType.STRING)
    private AttributeType type;

    @JsonDeserialize(using = AttributeValueDeserializer.class)
    @Transient
    private Object value;

    @JsonIgnore
    private String jsonValue;


    @PostLoad
    @PostUpdate
    @PostPersist
    private void setValue() throws IOException {
        ObjectMapper objectMapper = ApplicationContextUtils.getBean(ObjectMapper.class);
        value = objectMapper.readValue(jsonValue, type.getValueClass());
    }

}
