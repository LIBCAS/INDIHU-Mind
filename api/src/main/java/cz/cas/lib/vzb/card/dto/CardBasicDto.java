package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.util.converters.CardSimpleConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple version of Card for API response, to prevent recursion in relationships without @JsonIgnore
 *
 * @see CardSimpleConverter
 * @see Card
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
