package cz.cas.lib.vzb.card.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttributeHighlightDto {
    private String id;
    private String name;
    private String highlight;
}
