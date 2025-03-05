package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.jms.JmsException;

@ApplicationScoped
public class DirectResendToActiveMqRoute extends RouteBuilder {

    private static final String QUEUE_NAME = "jms:queue:messageQueue";
    private static final String DLQ_NAME = "jms:queue:DLQ"; // Dead Letter Queue

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel(DLQ_NAME)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .onRedelivery(exchange -> {
                    int retryCount = exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);
                    log.warn("Retrying message (attempt {}): {}", retryCount, exchange.getIn().getBody());
                }));

        // Specific exception handling
        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Message transformation failed due to illegal character: ${body}")
                .to(DLQ_NAME);

        onException(JmsException.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .handled(true)
                .log(LoggingLevel.ERROR, "Connection lost to ActiveMQ. Retrying...")
                .to(DLQ_NAME);

        // Route to send messages to ActiveMQ
        from("direct:sendToActiveMQ")
                .log("Sending message to ActiveMQ... Received message: ${body}")
                .split(body())
                    .log("Processing message: ${body}")
                    .doTry()
                        .to(QUEUE_NAME)
                        .log("Message sent successfully to ActiveMQ: ${body}")
                    .end();
    }
}