package cz.cas.lib.indihumind.security.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.domain.DatedObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "vzb_user")
public class User extends DatedObject {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

    private boolean allowed;

}
