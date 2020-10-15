package cz.cas.lib.vzb.reference.marc.record;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.vzb.dto.validation.Uuid;
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
