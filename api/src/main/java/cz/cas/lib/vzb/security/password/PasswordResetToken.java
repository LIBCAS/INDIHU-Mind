package cz.cas.lib.vzb.security.password;

import core.domain.DomainObject;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_password_token")
@Entity
public class PasswordResetToken extends DomainObject {
    private Instant expirationTime;
    private boolean utilized;
    @ManyToOne
    private User owner;
}
