package cz.cas.lib.indihumind.card.dto;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeHighlightDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
public class CardSearchResultDto {
    private CardRef card;
    private Map<String, Set<String>> highlightMap = new HashMap<>();
    private List<AttributeHighlightDto> highlightedAttributes = new ArrayList<>();

    public CardSearchResultDto(CardRef card) {
        this.card = card;
    }


    public void addHighlightElement(String fieldName, Collection<String> fieldHighlights) {
        if (fieldName == null || fieldHighlights == null || fieldHighlights.isEmpty()) return;
        highlightMap.put(fieldName, new HashSet<>(fieldHighlights));
    }

    public void addHighlightedAttribute(Attribute attribute, String highlight) {
        if (attribute == null) return;
        highlightedAttributes.add(new AttributeHighlightDto(attribute.getId(), attribute.getName(), highlight));
    }

}
