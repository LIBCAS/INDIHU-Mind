package cz.cas.lib.indihumind.security.password;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.indihumind.security.user.User;
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
public class PasswordToken extends DomainObject {

    private Instant expirationTime;

    /**
     * Flag to prevent using single token multiple time within expiration range.
     */
    private boolean utilized;

    @ManyToOne
    private User owner;

    @JsonIgnore
    public boolean isNotUtilized() {
        return !utilized;
    }

}
