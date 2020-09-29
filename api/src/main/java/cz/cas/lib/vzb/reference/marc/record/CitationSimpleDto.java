package cz.cas.lib.vzb.reference.marc.record;

import cz.cas.lib.vzb.util.converters.CitationSimpleConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Simple version of MarcRecord for API response, to prevent recursion in relationships without @JsonIgnore
 *
 * @see CitationSimpleConverter
 * @see Citation
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitationSimpleDto {
    private String id;
    private String name;
}
