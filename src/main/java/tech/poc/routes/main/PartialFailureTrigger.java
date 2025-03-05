package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.ProducerTemplate;

import java.util.List;

@ApplicationScoped
public class PartialFailureTrigger {

    private final ProducerTemplate producerTemplate;

    public PartialFailureTrigger(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void triggerRoute() {
        List<String> jsonMessages = List.of(
                "{\"userId\": \"123\", \"messageType\": \"notification\", \"payload\": \"Hello, World!\"}",
                "{\"userId\": \"456\", \"messageType\": \"alert\", \"payload\": \"Important message\"}",
                "{\"userId\": \"789\", \"messageType\": \"error\"}",
                "{\"userId\": \"999\", \"messageType\": \"error\", \"payload\": \"Invalid JSON test\"",
                "{\"userId\": \"888\", \"messageType\": \"error\", \"payload\": \"Bad char: \\u0000\"}"
        );

        producerTemplate.sendBody("direct:processJsonBatch", jsonMessages);
    }
}