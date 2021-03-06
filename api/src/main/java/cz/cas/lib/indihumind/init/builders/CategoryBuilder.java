package cz.cas.lib.indihumind.init.builders;

import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.security.user.User;

/**
 * Generated by Builder Generator plugin.
 * Can be re-generated manually if changes to attributes occur.
 */
public final class CategoryBuilder {
    private final Category category;

    private CategoryBuilder() {
        category = new Category();
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public CategoryBuilder name(String name) {
        category.setName(name);
        return this;
    }

    public CategoryBuilder ordinalNumber(int ordinalNumber) {
        category.setOrdinalNumber(ordinalNumber);
        return this;
    }

    public CategoryBuilder parent(Category parent) {
        category.setParent(parent);
        return this;
    }

    public CategoryBuilder owner(User owner) {
        category.setOwner(owner);
        return this;
    }

    public CategoryBuilder id(String id) {
        category.setId(id);
        return this;
    }

    public Category build() {
        return category;
    }

}
