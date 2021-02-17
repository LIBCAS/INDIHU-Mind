package cz.cas.lib.indihumind.util.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.dto.CardBasicDto;

public class CardSimpleConverter extends StdConverter<Card, CardBasicDto> {

    @Override
    public CardBasicDto convert(Card value) {
        if (value == null)
            return null;

        CardBasicDto dto = new CardBasicDto();
        dto.setId(value.getId());
        dto.setPid(value.getPid());
        dto.setName(value.getName());
        dto.setRawNote(value.getRawNote());
        return dto;
    }
}
