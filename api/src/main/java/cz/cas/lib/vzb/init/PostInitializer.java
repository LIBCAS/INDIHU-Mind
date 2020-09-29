package cz.cas.lib.vzb.init;

import core.index.global.GlobalReindexer;
import core.store.Transactional;
import cz.cas.lib.vzb.card.CardStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class PostInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${env}") private String env;
    @Inject private TestDataFiller testDataFiller;
    @Inject private GlobalReindexer globalReindexer;
    @Inject private CardStore cardStore;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info(String.format("Starting application in mode: [%s]", env)); // staging / deploy / test

            if ("staging".equals(env)) {
                testDataFiller.clearDatabase();
                testDataFiller.createUsersAndData();
                globalReindexer.reindex();
                cardStore.dropReindex();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
