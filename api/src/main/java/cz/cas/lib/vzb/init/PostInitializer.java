package cz.cas.lib.vzb.init;

import core.store.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class PostInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${env}")
    private String env;
    @Inject
    private TestDataFiller testDataFiller;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if ("staging".equals(env)) {
                testDataFiller.clear();
                testDataFiller.fill();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
