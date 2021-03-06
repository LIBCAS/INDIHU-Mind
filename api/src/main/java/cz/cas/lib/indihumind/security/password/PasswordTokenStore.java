package cz.cas.lib.indihumind.security.password;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordTokenStore extends DomainStore<PasswordToken, QPasswordToken> {
    public PasswordTokenStore() {
        super(PasswordToken.class, QPasswordToken.class);
    }
}
