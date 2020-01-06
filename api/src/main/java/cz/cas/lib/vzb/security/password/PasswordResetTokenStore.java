package cz.cas.lib.vzb.security.password;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetTokenStore extends DomainStore<PasswordResetToken, QPasswordResetToken> {
    public PasswordResetTokenStore() {
        super(PasswordResetToken.class, QPasswordResetToken.class);
    }
}
