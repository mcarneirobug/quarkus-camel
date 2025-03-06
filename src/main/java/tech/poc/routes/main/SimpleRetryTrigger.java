package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.ProducerTemplate;

@ApplicationScoped
public class SimpleRetryTrigger {

    private final ProducerTemplate producerTemplate;

    public SimpleRetryTrigger(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void triggerRoute() {
        producerTemplate.sendBody("direct:simple-retry", "This will fail");
    }
}