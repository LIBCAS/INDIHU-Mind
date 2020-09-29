package cz.cas.lib.vzb.search.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.vzb.search.validation.IndexedClassNameValidation;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_search_query")
@Entity
public class Query extends DomainObject {

    @ManyToOne
    @JsonIgnore
    private User owner;

    @NotBlank
    private String name;

    @IndexedClassNameValidation
    private String indexedClassName;

    @Convert(converter = QueryParamsJsonConverter.class)
    @Column(length = 4095)
    private QueryParams parameters;

}
