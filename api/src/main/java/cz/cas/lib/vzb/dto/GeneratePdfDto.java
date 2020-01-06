package cz.cas.lib.vzb.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for generating PDF from given id of {@link cz.cas.lib.vzb.reference.template.ReferenceTemplate}
 * and list of ids of {@link cz.cas.lib.vzb.reference.marc.Record}
 */
@Getter
@Setter
public class GeneratePdfDto {
    @NotBlank
    private String templateId;
    @NotEmpty
    private List<String> ids = new ArrayList<>();
}
