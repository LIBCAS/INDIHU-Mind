package cz.cas.lib.vzb.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.dto.CardBasicDto;

public class CardSimpleConverter extends StdConverter<Card, CardBasicDto> {

    @Override
    public CardBasicDto convert(Card value) {
        if (value == null)
            return null;
        return new CardBasicDto(value.getId(), value.getName(), value.getNote(), value.getPid());
    }
}
