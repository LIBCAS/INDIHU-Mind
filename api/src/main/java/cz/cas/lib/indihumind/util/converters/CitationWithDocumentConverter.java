package cz.cas.lib.indihumind.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.dto.CitationWithDocumentsDto;
import cz.cas.lib.indihumind.util.projection.EntityProjection;

import java.util.stream.Collectors;

// TODO probably remove after references are implemented
public class CitationWithDocumentConverter extends StdConverter<Citation, CitationWithDocumentsDto> {

    @Override
    public CitationWithDocumentsDto convert(Citation value) {
        if (value == null)
            return null;

        CitationWithDocumentsDto dto = new CitationWithDocumentsDto();
        dto.setId(value.getId());
        dto.setName(value.getName());
        dto.setDocuments(value.getDocuments().stream().map(EntityProjection::toEntity).collect(Collectors.toSet()));

        return dto;
    }
}
