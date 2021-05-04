package core.config;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SolrPersistentEntitySchemaCreator;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import java.util.Collections;

@Configuration
@EnableSolrRepositories(basePackages = {"cz.cas.lib.core", "cz.cas.lib.vzb"})
public class SolrConfig {

    @Value("${vzb.index.endpoint}")
    private String endpoint;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder().withBaseSolrUrl(endpoint).build();
    }

    @Bean
    @Primary
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
        SolrTemplate template = new SolrTemplate(client);
        template.setSchemaCreationFeatures(Collections.singletonList(SolrPersistentEntitySchemaCreator.Feature.CREATE_MISSING_FIELDS));
        template.afterPropertiesSet();
        return template;
    }

}