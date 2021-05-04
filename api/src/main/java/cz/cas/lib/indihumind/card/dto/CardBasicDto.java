package cz.cas.lib.indihumind.card.dto;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.util.converters.CardSimpleConverter;
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
    private String rawNote;
    private Card.CardStatus status;
    private long pid;
}
