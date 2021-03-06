package core.security.password;

import org.passay.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodPasswordGenerator {
    private Integer minPasswordLength;

    private Boolean requireDigit;

    private Boolean requireAlphabet;

    private List<Rule> rules;
    private List<CharacterRule> characterRules;

    @Inject
    public GoodPasswordGenerator(@Value("${security.password.length}") Integer minPasswordLength,
                                 @Value("${security.password.digit}") Boolean requireDigit,
                                 @Value("${security.password.alphabet}") Boolean requireAlphabet) {
        this.minPasswordLength = minPasswordLength;
        this.requireDigit = requireDigit;
        this.requireAlphabet = requireAlphabet;

        LengthRule lengthRule = new LengthRule(minPasswordLength, Integer.MAX_VALUE);
        WhitespaceRule whitespaceRule = new WhitespaceRule();

        // control allowed characters
        characterRules = new ArrayList<>();
        if (requireDigit) {
            characterRules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        }

        if (requireAlphabet) {
            characterRules.add(new CharacterRule(EnglishCharacterData.Alphabetical, 1));
        }

        CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
        charRule.getRules().addAll(characterRules);
        charRule.setNumberOfCharacteristics(charRule.getRules().size());

        // group all rules together in a List
        rules = new ArrayList<>();
        rules.add(lengthRule);
        rules.add(whitespaceRule);
        rules.add(charRule);
    }

    public boolean isValid(String password) {
        PasswordValidator validator = new PasswordValidator(rules);
        PasswordData passwordData = new PasswordData(password);

        RuleResult result = validator.validate(passwordData);
        return result.isValid();
    }

    public String generate() {
        PasswordGenerator generator = new PasswordGenerator();
        return generator.generatePassword(minPasswordLength, characterRules);
    }
}
