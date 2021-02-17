package cz.cas.lib.indihumind.citation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.Datafield;
import cz.cas.lib.indihumind.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
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
