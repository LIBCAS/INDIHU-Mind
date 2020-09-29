package cz.cas.lib.vzb.security.password;

import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserStore;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import static core.util.Utils.notNull;

@Service
public class PasswordTokenService {

    private long expirationInMinutes;

    private PasswordTokenStore store;
    private UserStore userStore;

    public PasswordToken find(String id) {
        PasswordToken passwordToken = store.find(id);
        notNull(passwordToken, () -> new MissingObject(PasswordToken.class, id));
        return passwordToken;
    }

    @Transactional
    public PasswordToken generateNewToken(@NonNull String email) {
        User user = userStore.findByEmail(email);
        notNull(user, () -> new MissingObject(User.class, email));

        PasswordToken token = new PasswordToken();
        token.setExpirationTime(Instant.now().plus(expirationInMinutes, ChronoUnit.MINUTES));
        token.setOwner(user);
        return store.save(token);
    }

    @Transactional
    public void utilizeToken(PasswordToken token) {
        token.setUtilized(true);
        store.save(token);
    }

    public Collection<PasswordToken> listAll() {
        return store.findAll();
    }

    public boolean isTokenValid(PasswordToken token) {
        return token.getExpirationTime().isAfter(Instant.now()) && token.isNotUtilized();
    }


    @Inject
    public void setExpirationInMinutes(@Value("${vzb.token.expirationTime}") long expirationInMinutes) {
        this.expirationInMinutes = expirationInMinutes;
    }

    @Inject
    public void setStore(PasswordTokenStore store) {
        this.store = store;
    }

    @Inject
    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

}
