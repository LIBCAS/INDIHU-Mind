package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.card.attachment.validation.ValidUploadAttachmentFileDto;
import cz.cas.lib.vzb.dto.validation.Uuid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ValidUploadAttachmentFileDto
public class UploadAttachmentFileDto {
    @Uuid
    private String id;
    private String cardId;
    private AttachmentFileProviderType providerType;
    private String name;
    private String type;
    private int ordinalNumber;
    /**
     * for external files only
     */
    private String providerId;
    /**
     * for external files only
     */
    private String link;
    /**
     * for local files only
     */
    private MultipartFile content;
}
