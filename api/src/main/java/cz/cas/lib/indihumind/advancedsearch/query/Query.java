package cz.cas.lib.indihumind.advancedsearch.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.domain.DomainObject;
import cz.cas.lib.indihumind.advancedsearch.validation.IndexedClassNameValidation;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
    @Size(max = 255, message = "Max allowed length (=255) exceeded.")
    private String name;

    @IndexedClassNameValidation
    private String indexedClassName;

    @Convert(converter = QueryParamsJsonConverter.class)
    @Column(length = 4095)
    private QueryParams parameters;

}
