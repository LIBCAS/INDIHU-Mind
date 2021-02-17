package cz.cas.lib.indihumind.citationtemplate.fields.author;

public class CompanyAuthor implements Author {

    private final String companyName;

    public CompanyAuthor(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String nameReversedOrder() {
        return companyName.toUpperCase();
    }

    @Override
    public String nameInOrder() {
        return companyName.toUpperCase();
    }
}
