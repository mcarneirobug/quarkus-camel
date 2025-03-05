package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.ProducerTemplate;

@ApplicationScoped
public class DirectResendTrigger {

    private final ProducerTemplate producerTemplate;

    public DirectResendTrigger(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void sendMessage(String message) {
        producerTemplate.sendBody("direct:sendToActiveMQ", message);
        System.out.println("Message sent: " + message);
    }
}
