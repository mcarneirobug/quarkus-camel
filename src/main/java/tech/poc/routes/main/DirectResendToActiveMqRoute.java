package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class DirectResendToActiveMqRoute extends RouteBuilder {

    private static final String QUEUE_NAME = "jms:queue:messageQueue";
    private static final String DLQ_NAME = "jms:queue:DLQ"; // Dead Letter Queue
    public static final String UNKNOWN = "UNKNOWN";
    public static final String USER_ID = "userId";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String PAYLOAD = "payload";

    @Override
    public void configure() throws Exception {

    }
}
