package cz.cas.lib.vzb.reference.marc;

import com.fasterxml.jackson.databind.util.StdConverter;

public class RecordSimpleConverter extends StdConverter<Record, RecordSimpleDto> {

    @Override
    public RecordSimpleDto convert(Record value) {
        if (value == null)
            return null;
        return new RecordSimpleDto(value.getId(), value.getName());
    }
}
