package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.attribute.AttributeHighlightDto;
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
