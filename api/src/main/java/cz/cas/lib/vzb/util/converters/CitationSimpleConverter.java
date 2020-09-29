package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.record.CitationSimpleDto;

public class CitationSimpleConverter extends StdConverter<Citation, CitationSimpleDto> {

    @Override
    public CitationSimpleDto convert(Citation value) {
        if (value == null)
            return null;

        CitationSimpleDto dto = new CitationSimpleDto();
        dto.setId(value.getId());
        dto.setName(value.getName());

        return dto;
    }
}
