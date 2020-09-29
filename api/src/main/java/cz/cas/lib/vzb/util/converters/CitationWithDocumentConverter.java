package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.record.CitationWithDocumentsDto;

public class CitationWithDocumentConverter extends StdConverter<Citation, CitationWithDocumentsDto> {

    @Override
    public CitationWithDocumentsDto convert(Citation value) {
        if (value == null)
            return null;

        CitationWithDocumentsDto dto = new CitationWithDocumentsDto();
        dto.setId(value.getId());
        dto.setName(value.getName());
        dto.setDocuments(value.getDocuments());

        return dto;
    }
}
