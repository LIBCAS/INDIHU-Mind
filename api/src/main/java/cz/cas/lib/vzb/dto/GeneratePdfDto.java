package cz.cas.lib.vzb.dto;

import cz.cas.lib.vzb.dto.validation.Uuid;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.template.ReferenceTemplate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for generating PDF from given id of {@link ReferenceTemplate}
 * and list of ids of {@link Citation}
 */
@Getter
@Setter
public class GeneratePdfDto {

    @NotBlank private String templateId;
    @NotEmpty private List<@Uuid String> ids = new ArrayList<>();

}
