package cz.cas.lib.vzb.card.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.domain.NamedObject;
import cz.cas.lib.vzb.card.Card;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "providerType",
        defaultImpl = ExternalAttachmentFile.class,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LocalAttachmentFile.class, name = "LOCAL")})
public abstract class AttachmentFile extends NamedObject {
    private String type;
    private int ordinalNumber;
    @JsonIgnore
    @ManyToOne
    private Card card;
}
