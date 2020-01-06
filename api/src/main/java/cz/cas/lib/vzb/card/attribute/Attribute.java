package cz.cas.lib.vzb.card.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import core.domain.DomainObject;
import core.util.ApplicationContextUtils;
import cz.cas.lib.vzb.card.CardContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
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

//    public Attribute(Attribute attribute, V value) {
//        super();
//        setCard(attribute.getCard());
//        setName(attribute.getName());
//        setOrdinalNumber(attribute.getOrdinalNumber());
//        setValue(value);
//    }

//    public Attribute(AttributeTemplate attributeTemplate, Card c) {
//        super();
//        setCard(c);
//        setName(attributeTemplate.getName());
//        setOrdinalNumber(attributeTemplate.getOrdinalNumber());
//    }

//    public static Attribute createInstance(AttributeType attributeType) {
//        switch (attributeType) {
//            case DATETIME:
//                return new DateTimeAttribute();
//            case BOOLEAN:
//                return new BooleanAttribute();
//            case STRING:
//                return new StringAttribute();
//            case DOUBLE:
//                return new DoubleAttribute();
//            case INTEGER:
//                return new IntegerAttribute();
//            default:
//                throw new IllegalArgumentException();
//        }
//    }

//    public static Class getValueClass(Object o) {
//        o.getClass()
//        if (o instanceof Instant) {
//            return Instant
//        }
//        switch (type) {
//            case DATETIME:
//                return Instant.class;
//            case BOOLEAN:
//                return new BooleanAttribute();
//            case STRING:
//                return new StringAttribute();
//            case DOUBLE:
//                return new DoubleAttribute();
//            case INTEGER:
//                return new IntegerAttribute();
//            default:
//                throw new IllegalArgumentException();
//        }
//    }
//
//    @PostLoad
//    public void setValue() {
//
//        Category c = getTeam().getCategory();
//        if (c == Category.MEN) {
//            playerOlderThanTeamLimit = false;
//            return;
//        }
//        if (!LocalDate.now().minusYears(c.getUpperAgeLimit()).isBefore(getPlayer().getDateOfBirth())) {
//            playerOlderThanTeamLimit = true;
//            return;
//        }
//        playerOlderThanTeamLimit = false;
//
//        if (!LocalDate.now().minusYears(c.getBottomAgeLimit()).isAfter(getPlayer().getDateOfBirth())) {
//            playerYoungerThanTeamLimit = true;
//            return;
//        }
//        playerYoungerThanTeamLimit = false;
//    }
}
