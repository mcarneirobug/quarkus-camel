package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.ProducerTemplate;

import java.util.List;

@ApplicationScoped
public class TransientErrorTrigger {

    private final ProducerTemplate producerTemplate;

    public TransientErrorTrigger(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void triggerRoute() {
        List<String> jsonMessages = List.of(
                "{\"userId\": \"123\", \"messageType\": \"notification\", \"payload\": \"Hello, World!\"}",
                "{\"userId\": \"456\", \"messageType\": \"alert\", \"payload\": \"Important message\"}"
        );

        producerTemplate.sendBody("direct:sendToQueue", jsonMessages);
    }
}
