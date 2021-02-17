package cz.cas.lib.indihumind.document.dto;

import cz.cas.lib.indihumind.document.AttachmentFileProviderType;
import cz.cas.lib.indihumind.document.UrlAttachmentFile;
import cz.cas.lib.indihumind.validation.Uuid;
import cz.cas.lib.indihumind.validation.ValidCreateAttachmentFileDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    @ApiModelProperty(value = "Name of attached file", required = true, position = 3, example = "my_fabulous_file_name")
    private String name;

    @ApiModelProperty(value = "Provider of attachment", required = true, position = 4, example = "LOCAL")
    private AttachmentFileProviderType providerType;

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    @ApiModelProperty(value = "Type of file (or extension)", required = true, position = 5, example = "png")
    private String type;

    /**
     * for external files only
     */
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    @ApiModelProperty(value = "ID of file assigned by provider", position = 6, notes = "Use only for External files")
    private String providerId;

    /**
     * for external and url attachments only
     */
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    @ApiModelProperty(value = "URL link to external file", position = 7, notes = "Use for URL or External files")
    private String link;

    @ApiModelProperty(value = "Flag describes whether to download and store a URL file", position = 8, notes = "Use for URL files only")
    private UrlAttachmentFile.UrlDocumentLocation location;


    public boolean shouldDownloadUrlDocumentFromLink() {
        return location == UrlAttachmentFile.UrlDocumentLocation.SERVER;
    }
}
