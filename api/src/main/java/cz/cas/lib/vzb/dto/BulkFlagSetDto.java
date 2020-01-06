package cz.cas.lib.vzb.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * general DTO for bulk updates of particular flag of multiple entities of particular type
 */
@Getter
@Setter
public class BulkFlagSetDto {
    private List<String> ids = new ArrayList<>();
    @NotNull
    private Boolean value;
}
