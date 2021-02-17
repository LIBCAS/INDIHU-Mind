package cz.cas.lib.indihumind.card.dto;

import cz.cas.lib.indihumind.cardattribute.Attribute;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateCardContentDto {
    private List<@Valid Attribute> attributes = new ArrayList<>();
    private boolean newVersion;
}
