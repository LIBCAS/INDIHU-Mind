package core.index.nested;

import core.domain.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "test_nested_child")
@NoArgsConstructor
@AllArgsConstructor
public class ChildEntity extends DomainObject {

    @ManyToOne
    private ParentEntity parent;
    private String attr;
}
