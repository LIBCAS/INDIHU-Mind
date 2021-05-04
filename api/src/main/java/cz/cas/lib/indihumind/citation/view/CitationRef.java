package cz.cas.lib.indihumind.citation.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @see Citation
 */
@Immutable
@Getter
@Entity(name = "citation_ref")
@Table(name = "vzb_marc_citation")
@JsonPropertyOrder({"id", "name", "created", "updated", "deleted"})
public class CitationRef extends DatedObject implements EntityProjection<Citation> {

    private String name;

    @Override
    public Citation toEntity() {
        Citation entity = new Citation();
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);

        entity.setName(name);
        return entity;
    }

}
