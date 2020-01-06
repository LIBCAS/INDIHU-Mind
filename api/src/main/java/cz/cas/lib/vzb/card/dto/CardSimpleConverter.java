package cz.cas.lib.vzb.card.dto;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.vzb.card.Card;

public class CardSimpleConverter extends StdConverter<Card, CardBasicDto> {

    @Override
    public CardBasicDto convert(Card value) {
        if (value == null)
            return null;
        return new CardBasicDto(value.getId(), value.getName(), value.getNote(), value.getPid());
    }
}
