package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.util.converters.AttachmentFileSimpleConverter;
import cz.cas.lib.vzb.util.converters.CitationWithDocumentConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


/**
 * Simple version of MarcRecord for API response, to prevent recursion in relationships without @JsonIgnore
 *
 * @see CitationWithDocumentConverter
 * @see Citation
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitationWithDocumentsDto {
    private String id;
    private String name;
    @JsonSerialize(contentConverter = AttachmentFileSimpleConverter.class)
    private Set<AttachmentFile> documents = new HashSet<>();
}
