package cz.cas.lib.vzb.attachment;

import cz.cas.lib.vzb.attachment.validation.ValidCreateAttachmentFileDto;
import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ValidCreateAttachmentFileDto
public class CreateAttachmentDto {

    @ApiModelProperty(value = "IDs of cards that this attachment is for", position = 1)
    private List<@Uuid String> linkedCards = new ArrayList<>();

    @ApiModelProperty(value = "IDs of records that this attachment is for", position = 2)
    private List<@Uuid String> records = new ArrayList<>();

    @NotBlank
    @ApiModelProperty(value = "Name of attached file", required = true, position = 3, example = "my_fabulous_file_name")
    private String name;

    @ApiModelProperty(value = "Provider of attachment", required = true, position = 4, example = "LOCAL")
    private AttachmentFileProviderType providerType;

    @ApiModelProperty(value = "Type of file (or extension)", required = true, position = 5, example = "png")
    private String type;

    /**
     * for external files only
     */
    @ApiModelProperty(value = "ID of file assigned by provider", position = 6, notes = "Use only for External files")
    private String providerId;

    /**
     * for external and url attachments only
     */
    @ApiModelProperty(value = "URL link to external file", position = 7, notes = "Use for URL or External files")
    private String link;

}
