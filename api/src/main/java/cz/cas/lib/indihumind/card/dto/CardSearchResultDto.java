package cz.cas.lib.indihumind.card.dto;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.cardattribute.AttributeHighlightDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
public class CardSearchResultDto {
    private Card card;
    private Map<String, Set<String>> highlightMap = new HashMap<>();
    private List<AttributeHighlightDto> highlightedAttributes = new ArrayList<>();
}
