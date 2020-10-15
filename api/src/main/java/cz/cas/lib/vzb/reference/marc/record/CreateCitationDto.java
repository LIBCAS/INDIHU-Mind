package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @see Citation
 * @see UpdateCitationDto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "content", "dataFields"})
public class CreateCitationDto {

    @NotBlank
    @ApiModelProperty(value = "Name of citation", required = true)
    private String name;

    @ApiModelProperty(value = "IDs of linked documents")
    private List<@Uuid String> documents = new ArrayList<>();

    @ApiModelProperty(value = "IDs of linked cards")
    private List<@Uuid String> linkedCards = new ArrayList<>();

    @ApiModelProperty(value = "MARC datafields")
    private List<Datafield> dataFields = new ArrayList<>();

    @ApiModelProperty(value = "Copy-pasted text")
    private String content;

}
