package cz.cas.lib.indihumind.citation.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @see Citation
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id", "name", "content", "dataFields"})
public class UpdateCitationDto extends CreateCitationDto {

    @Uuid
    @NotBlank
    @ApiModelProperty(value = "ID of citation", required = true)
    private String id;

}
