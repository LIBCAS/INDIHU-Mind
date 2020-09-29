package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.attachment.AttachmentFileSimpleDto;

public class AttachmentFileSimpleConverter extends StdConverter<AttachmentFile, AttachmentFileSimpleDto> {

    @Override
    public AttachmentFileSimpleDto convert(AttachmentFile value) {
        if (value == null)
            return null;

        AttachmentFileSimpleDto dto = new AttachmentFileSimpleDto();
        dto.setId(value.getId());
        dto.setName(value.getName());
        dto.setProviderType(value.getProviderType());
        dto.setType(value.getType());
        dto.setLinkedCards(value.getLinkedCards());
        dto.setRecords(value.getRecords());

        return dto;
    }
}
