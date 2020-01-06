package core.security.authorization.assign;

import core.Changed;
import core.domain.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "uas_assigned_role")
@AllArgsConstructor
@NoArgsConstructor
@Changed("role entity replaced with hardcoded string")
public class AssignedRole extends DomainObject {
    protected String userId;
    protected String role;
}
