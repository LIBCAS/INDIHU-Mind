package cz.cas.lib.indihumind.citationtemplate.fields.author.option;

import lombok.Getter;

public enum AndJoiner {
    ENGLISH_AND("and"), // Surname, N., Surname, N., Surname, N. and Surname, N.
    CZECH_AND("a"),     // Surname, N., Surname, N., Surname, N. a Surname, N.
    AMPERSAND("&");     // Surname, N., Surname, N., Surname, N. & Surname, N.

    @Getter
    private final String symbol;

    AndJoiner(String symbol) {
        this.symbol = symbol;
    }
}
