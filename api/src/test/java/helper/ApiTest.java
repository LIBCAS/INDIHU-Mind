package helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.rest.config.ResourceExceptionHandler;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Slf4j
@Component
public abstract class ApiTest implements AlterSolrCollection {

    @Inject private Filter springSecurityFilterChain;
    @Inject private WebApplicationContext webApplicationContext;
    @Inject private DataSource dataSource;
    @Inject private SolrClient solrClient;
    @Inject protected ObjectMapper objectMapper;
    @Inject protected TransactionTemplate transactionTemplate;

    /** Name of test collection for {@link cz.cas.lib.vzb.card.IndexedCard} */
    @Getter
    @Value("${vzb.index.cardCollectionName}")
    private String cardTestCollectionName;

    /** Name of test collection for entities extending {@link core.index.IndexedDomainObject} */
    @Getter
    @Value("${vzb.index.uasTestCollectionName}")
    private String uasTestCollectionName;


    @Before
    public void alterSolrCollection() {
        modifySolrDocumentAnnotationForIndexedClasses();
    }

    private ObjectMapper mapper() {
        return new ObjectMapper();
    }

    protected String toDefaultJson(Object o) throws JsonProcessingException {
        return mapper().writeValueAsString(o);
    }

    protected MockMvc mvc(Object controller) {
        return standaloneSetup(controller)
                .setControllerAdvice(new ResourceExceptionHandler())
                .build();
    }

    protected MockMvc securedMvc() {
        if (springSecurityFilterChain == null || webApplicationContext == null) {
            throw new UnsupportedOperationException("implementing class must be a springboot test");
        }
        return webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain).build();
    }

    protected RequestPostProcessor mockedUser(String id, String... roles) {
        User u = new User();
        u.setId(id);
        Collection<GrantedAuthority> authorities = null;
        if (roles != null)
            authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return SecurityMockMvcRequestPostProcessors.user(new UserDelegate(u, authorities));
    }


    @After
    public void apiTestTearDown() throws Exception {
        if (dataSource != null) {
            Connection c = dataSource.getConnection();
            Statement s = c.createStatement();
            s.execute("TRUNCATE SCHEMA PUBLIC AND COMMIT");
            s.close();
            c.close();
        }
        if (solrClient != null) {
            log.info("Deleting indices from collection: " + cardTestCollectionName);
            solrClient.deleteByQuery(cardTestCollectionName, "*:*");
            solrClient.commit(cardTestCollectionName);

            log.info("Deleting indices from collection: " + uasTestCollectionName);
            solrClient.deleteByQuery(uasTestCollectionName, "*:*");
            solrClient.commit(uasTestCollectionName);
        }
    }
}
