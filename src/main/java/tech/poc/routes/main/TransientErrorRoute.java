package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

@ApplicationScoped
public class TransientErrorRoute extends RouteBuilder {
    private static final String QUEUE_NAME = "jms:queue:messageQueue";
    private static final String INSERT_FAILED_MESSAGE = "INSERT INTO failed_messages (user_id, message_type, payload, status, error_message) VALUES (:?userId, :?messageType, :?payload, 'FAILED', :?errorMessage)";
    private static final String SELECT_FAILED_MESSAGES = "SELECT * FROM failed_messages WHERE status = 'FAILED'";

    private static final String UNKNOWN = "UNKNOWN";
    private static final String USER_ID = "userId";
    private static final String MESSAGE_TYPE = "messageType";
    private static final String PAYLOAD = "payload";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ID = "id";

    @Override
    public void configure() throws Exception {
        from("direct:sendToQueue")
                .log("Sending message to ActiveMQ... Received message: ${body}")
                .split(body())
                .log("Received message split: ${body}")
                .doTry()
                    .to(QUEUE_NAME)
                    .log("Message sent successfully to MessageHub: ${body}")
                .doCatch(Exception.class)
                    .log(LoggingLevel.ERROR, "ActiveMQ is down! Storing message in the database")
                    .unmarshal().json(JsonLibrary.Jackson)
                    .process(this::storeFailedMessage)
                    .log("Storing failed message with userId: ${header.userId}, messageType: ${header.messageType}, payload: ${header.payload}")
                    .setBody(constant(INSERT_FAILED_MESSAGE))
                    .to("jdbc:default?useHeadersAsParameters=true")
                .end();

        from("timer:retryFailedMessages?period=90000")
                .log("Checking for failed messages to retry...")
                .setBody(constant(SELECT_FAILED_MESSAGES))
                .to("jdbc:default?outputType=SelectList")
                .split(body())
                    .log("Retrying message: ${body}")
                    .process(this::setHeadersForRetry)
                    .marshal().json()
                    .to("direct:sendToQueue")
                    .log("Header id: {header.id}")
                    .choice()
                        .when(exchange -> exchange.getIn().getHeader("id") != null)
                            .process(exchange -> {
                                String updateQuery = "UPDATE failed_messages SET status = 'PROCESSED' WHERE id = " + exchange.getIn().getHeader(ID);
                                exchange.getIn().setBody(updateQuery);
                            })
                        .to("jdbc:default?useHeadersAsParameters=true")
                        .log("Message reprocessed and marked as PROCESSED: ID ${header.id}")
                    .otherwise()
                        .log(LoggingLevel.WARN, "Skipping message update due to missing ID")
                    .end();
    }

    /**
     * Store failed message details in headers for database insertion.
     */
    private void storeFailedMessage(Exchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> row = exchange.getIn().getBody(Map.class);
        exchange.getIn().setHeader(USER_ID, row.getOrDefault(USER_ID, UNKNOWN));
        exchange.getIn().setHeader(MESSAGE_TYPE, row.getOrDefault(MESSAGE_TYPE, UNKNOWN));
        exchange.getIn().setHeader(PAYLOAD, row.getOrDefault(PAYLOAD, UNKNOWN));
        exchange.getIn().setHeader(ERROR_MESSAGE, "Connection to ActiveMQ failed");

        exchange.getIn().setBody(INSERT_FAILED_MESSAGE);
    }

    /**
     * Set headers for retrying a failed message.
     */
    private void setHeadersForRetry(Exchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> row = exchange.getIn().getBody(Map.class);
        Object id = row.get("ID");

        exchange.getIn().setHeader(ID, id);
        exchange.getIn().setHeader(USER_ID, row.getOrDefault("user_id", UNKNOWN));
        exchange.getIn().setHeader(MESSAGE_TYPE, row.getOrDefault("message_type", UNKNOWN));
        exchange.getIn().setHeader(PAYLOAD, row.getOrDefault(PAYLOAD, UNKNOWN));
        exchange.getIn().setBody(row);
    }
}
