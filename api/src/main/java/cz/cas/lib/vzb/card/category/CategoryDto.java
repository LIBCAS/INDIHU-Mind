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

    public CategoryDto(Category source) {
        this.id = source.getId();
        this.ordinalNumber = source.getOrdinalNumber();
        this.name = source.getName();
        if (source.getParent() != null)
            this.parentId = source.getParent().getId();
    }

    public void addSubCategory(CategoryDto c) {
        subCategories.add(c);
    }

    public void incrementCardsCount(Long v) {
        this.cardsCount += v;
    }
}
