package cz.cas.lib.vzb.reference.marc.record;

import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @see Citation
 * @see UpdateCitationDto
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ValidCreateCitationDto
public class CreateCitationDto {

    @NotNull
    @ApiModelProperty(value = "Type of citation", required = true, allowableValues = "MARC, BRIEF")
    private CitationType type;

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
