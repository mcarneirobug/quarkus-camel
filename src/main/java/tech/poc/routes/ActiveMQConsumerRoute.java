package tech.poc.routes;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class ActiveMQConsumerRoute extends RouteBuilder {

    public static final String ROUTE_ID_ACTIVEMQ = "activemq-consumer-route";

    @Override
    public void configure() {
        from("jms:queue:processedMessages")
                .routeId(ROUTE_ID_ACTIVEMQ)
                .log("Received message directly: ${body}");
    }
}