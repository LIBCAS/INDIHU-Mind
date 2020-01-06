package cz.cas.lib.vzb.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple version of Card for API response, to prevent recursion in bidirectional relationships without @JsonIgnore
 * DTO used by {@link CardSimpleConverter}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardBasicDto {
    private String id;
    private String name;
    private String note;
    private long pid;
}
