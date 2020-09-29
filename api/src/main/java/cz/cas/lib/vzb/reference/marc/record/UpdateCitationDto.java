package cz.cas.lib.vzb.reference.marc.record;


import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @see Citation
 * @see CreateCitationDto
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCitationDto {

    @Uuid
    @NotBlank
    @ApiModelProperty(value = "ID of citation", required = true)
    private String id;

    @NotBlank
    @ApiModelProperty(value = "Name of citation", required = true)
    private String name;

    @ApiModelProperty(value = "IDs of linked documents")
    private List<@Uuid String> documents = new ArrayList<>();

    @ApiModelProperty(value = "IDs of linked cards")
    private List<@Uuid String> linkedCards = new ArrayList<>();

    @ApiModelProperty(value = "Datafields of MARC citation", notes = "Use only with type=MARC")
    private List<Datafield> dataFields = new ArrayList<>();

    @ApiModelProperty(value = "Copy-pasted text of BRIEF citation", notes = "Use only with type=BRIEF")
    private String content;

}
