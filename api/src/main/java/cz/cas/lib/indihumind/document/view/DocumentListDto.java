package cz.cas.lib.indihumind.document.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.AttachmentFileProviderType;
import cz.cas.lib.indihumind.document.LocalAttachmentFile;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @see Citation
 */
@Immutable
@Getter
@Entity(name = "document_list")
@Table(name = "vzb_attachment_abstract")
@JsonPropertyOrder({"id", "name", "providerType", "type", "created", "updated", "deleted", "linkedCards", "records"})
public class DocumentListDto extends DatedObject implements EntityProjection<AttachmentFile> {

    private String name;

    private String type;

    @Enumerated(value = EnumType.STRING)
    private AttachmentFileProviderType providerType;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "vzb_card_attachment",
            joinColumns = @JoinColumn(name = "attachment_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<CardRef> linkedCards = new HashSet<>();

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "vzb_citation_document",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "citation_id")
    )
    private Set<CitationRef> records = new HashSet<>();

    @Override
    public AttachmentFile toEntity() {
        // chosen subclass for instantiation does not matter because all fields are from superclass
        AttachmentFile entity = new LocalAttachmentFile();
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);

        entity.setName(name);
        entity.setType(type);
        entity.setProviderType(providerType);
        entity.setLinkedCards(linkedCards.stream().map(CardRef::toEntity).collect(Collectors.toSet()));
        entity.setRecords(records.stream().map(CitationRef::toEntity).collect(Collectors.toSet()));
        return entity;
    }

}
