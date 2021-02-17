package cz.cas.lib.indihumind.document.dto;

import cz.cas.lib.indihumind.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateAttachmentDto {

    @ApiModelProperty(value = "IDs of cards that this attachment is for", position = 1)
    private List<@Uuid String> linkedCards = new ArrayList<>();

    @ApiModelProperty(value = "IDs of records that this attachment is for", position = 2)
    private List<@Uuid String> records = new ArrayList<>();

    @NotBlank
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    @ApiModelProperty(value = "Name of attached file", required = true, position = 3, example = "updated_file_name")
    private String name;

}
