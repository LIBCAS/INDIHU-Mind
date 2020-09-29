package cz.cas.lib.vzb.attachment;

import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
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
    @ApiModelProperty(value = "Name of attached file", required = true, position = 3, example = "updated_file_name")
    private String name;

}
