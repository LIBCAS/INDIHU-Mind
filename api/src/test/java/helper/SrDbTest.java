package helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.config.ObjectMapperProducer;
import core.index.IndexedDatedStore;
import core.index.IndexedDomainStore;
import core.index.IndexedNamedStore;
import core.store.DomainStore;
import lombok.Getter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SolrPersistentEntitySchemaCreator;

import java.util.Collections;
import java.util.Properties;

public class SrDbTest extends DbTest {

    private static final Properties props = new Properties();
    private ObjectMapperProducer objectMapperProducer = new ObjectMapperProducer();

    @Getter
    private HttpSolrClient client;
    @Getter
    private SolrTemplate template;

    @BeforeClass
    public static void classSetUp() throws Exception {
        DbTest.dbTestClassSetUp();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        DbTest.dbTestClassTearDown();
    }

    /**
     * should have different name than its subclass or the method of subclass is not called
     */
    @Before
    public void srDbTestSetUp() throws Exception {
        ObjectMapper objectMapper = objectMapperProducer.objectMapper(false, false);

        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        String solrClientUrl = props.getProperty("vzb.index.endpoint");
        client = new HttpSolrClient.Builder().withBaseSolrUrl(solrClientUrl).build();

        template = new SolrTemplate(client);
        template.setSchemaCreationFeatures(Collections.singletonList(SolrPersistentEntitySchemaCreator.Feature.CREATE_MISSING_FIELDS));

        template.afterPropertiesSet();
    }

    /**
     * should have different name than its subclass or the method of subclass is not called
     *
     * @throws Exception
     */
    @After
    public void srDbTestTearDownn() throws Exception {
        client.deleteByQuery("uas-test", "*:*");
        client.commit("uas-test");
        client.close();
    }

    @Override
    public void initializeStores(DomainStore... stores) {
        for (DomainStore store : stores) {
            store.setEntityManager(super.getEm());
            store.setQueryFactory(new JPAQueryFactory(super.getEm()));
            if (store instanceof IndexedDomainStore) {
                IndexedDomainStore impl = (IndexedDomainStore) store;
                impl.init();
                impl.setTemplate(getTemplate());
                continue;
            }
            if (store instanceof IndexedDatedStore) {
                IndexedDatedStore impl = (IndexedDatedStore) store;
                impl.init();
                impl.setTemplate(getTemplate());
                continue;
            }
            if (store instanceof IndexedNamedStore) {
                IndexedNamedStore impl = (IndexedNamedStore) store;
                impl.setTemplate(getTemplate());
                impl.init();
            }

        }
    }
}
