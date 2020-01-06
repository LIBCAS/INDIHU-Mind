package core.store.general;

import core.domain.DomainObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "test_general")
public class GeneralTestEntity extends DomainObject {
    private String test;
    @OneToOne
    private GeneralTestEntity relatedObject;
}
