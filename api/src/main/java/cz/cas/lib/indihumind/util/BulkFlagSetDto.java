package cz.cas.lib.indihumind.util;

import cz.cas.lib.indihumind.validation.Uuid;
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
    private List<@Uuid  String> ids = new ArrayList<>();
    @NotNull
    private Boolean value;
}
