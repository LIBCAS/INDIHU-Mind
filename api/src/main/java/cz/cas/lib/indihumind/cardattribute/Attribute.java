package cz.cas.lib.indihumind.cardattribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import core.domain.DomainObject;
import core.util.ApplicationContextUtils;
import cz.cas.lib.indihumind.card.CardContent;
import cz.cas.lib.indihumind.util.converters.AttributeValueDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
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

}
