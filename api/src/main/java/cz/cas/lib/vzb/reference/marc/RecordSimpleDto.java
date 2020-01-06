package cz.cas.lib.vzb.reference.marc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Simple version of Record for API response, to prevent recursion in bidirectional relationships without @JsonIgnore
 * DTO used by {@link RecordSimpleConverter}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecordSimpleDto {
    private String id;
    private String name;
}
