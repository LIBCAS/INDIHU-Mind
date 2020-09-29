package cz.cas.lib.vzb.attachment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.util.converters.AttachmentFileSimpleConverter;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import cz.cas.lib.vzb.util.converters.CitationSimpleConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple version of MarcRecord for API response, to prevent recursion in relationships without @JsonIgnore
 *
 * @see AttachmentFileSimpleConverter
 * @see AttachmentFile
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentFileSimpleDto {
    private String id;
    private String name;
    private AttachmentFileProviderType providerType;
    private String type;
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();
    @JsonSerialize(contentConverter = CitationSimpleConverter.class)
    private Set<Citation> records = new HashSet<>();
}


