package cz.cas.lib.vzb.card.category;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class CategoryDto {
    private String id;
    private String name;
    private int ordinalNumber;
    private String parentId;
    private Set<CategoryDto> subCategories = new HashSet<>();
    private Long cardsCount = 0L;

    public void addSubCategory(CategoryDto c) {
        subCategories.add(c);
    }

    public CategoryDto(Category source) {
        setId(source.getId());
        setOrdinalNumber(source.getOrdinalNumber());
        setName(source.getName());
        if (source.getParent() != null)
            setParentId(source.getParent().getId());
    }

    public void incrementCardsCount(Long v) {
        this.cardsCount += v;
    }
}
