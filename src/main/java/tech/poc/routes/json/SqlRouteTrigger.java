package tech.poc.routes.json;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;

@ApplicationScoped
public class SqlRouteTrigger {

    @Inject
    ProducerTemplate producerTemplate;

    public void triggerRoute() {
        producerTemplate.sendBody("direct:insertMessage", null);
    }
}
