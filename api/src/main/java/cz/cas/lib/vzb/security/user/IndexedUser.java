package cz.cas.lib.vzb.security.user;

import core.index.IndexFieldType;
import core.index.IndexedDatedObject;
import cz.cas.lib.vzb.search.searchable.AdvancedSearch;
import cz.cas.lib.vzb.search.query.QueryType;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedUser extends IndexedDatedObject {

    public static final String EMAIL = "email";
    public static final String ALLOWED = "allowed";
    public static final String ROLES = "roles";


    @Field(value = EMAIL)
    @Indexed(type = IndexFieldType.STRING)
    @AdvancedSearch(czech = "E-mail", type = QueryType.STRING)
    private String email;

    @Field(value = ALLOWED)
    @Indexed(type = IndexFieldType.BOOLEAN)
    private boolean allowed;

    @Field(value = ROLES)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> roles;

}
