package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.attribute.Attribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateCardContentDto {
    private List<Attribute> attributes = new ArrayList<>();
    private boolean newVersion;
}
