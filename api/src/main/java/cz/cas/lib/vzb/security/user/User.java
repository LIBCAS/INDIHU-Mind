package cz.cas.lib.vzb.security.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import lombok.*;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "vzb_user")
public class User extends DatedObject {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private boolean allowed;
}
