package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PartialFailureRoute extends RouteBuilder {

    private static final String USER_ID = "userId";
    private static final String MESSAGE_TYPE = "messageType";
    private static final String PAYLOAD = "payload";
    private static final String RAW_PAYLOAD = "rawPayload";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String FAILED_STATUS = "FAILED";
    private static final String PROCESSED_STATUS = "PROCESSED";
    public static final String UNKNOWN = "UNKNOWN";

    @Override
    public void configure() throws Exception {
        from("direct:processJsonBatch")
                .log("Processing batch of JSON messages")
                .split(body())
                .doTry()
                    .process(this::storeRawPayload)
                    .unmarshal().json(JsonLibrary.Jackson)
                    .process(this::extractAndValidateJson)
                    .setBody(simple("INSERT INTO failed_messages (user_id, message_type, payload, status) VALUES (:?" + USER_ID + ", :?" + MESSAGE_TYPE + ", :?" + PAYLOAD + ", '" + PROCESSED_STATUS + "')"))
                    .to("jdbc:default?useHeadersAsParameters=true")
                    .log("Message processed successfully: ${body}")
                .doCatch(Exception.class)
                    .log("Error detected in JSON processing: ${exception.message}")
                    .to("direct:handleJsonFormatError")
                .end();

        from("direct:handleJsonFormatError")
                .process(this::handleJsonError)
                .to("jdbc:default?useHeadersAsParameters=true")
                .log("Malformed JSON stored in the database with " + FAILED_STATUS + " status.");
    }

    private void handleJsonError(Exchange exchange) {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        String errorMessage = exception != null ? exception.getMessage() : "Unknown JSON parsing error";
        String rawPayload = exchange.getIn().getHeader(RAW_PAYLOAD, String.class);
        String userId = exchange.getIn().getHeader(USER_ID, UNKNOWN, String.class);
        String messageType = exchange.getIn().getHeader(MESSAGE_TYPE, UNKNOWN, String.class);
        String payload = exchange.getIn().getHeader(PAYLOAD, UNKNOWN, String.class);

        exchange.getIn().setHeader(USER_ID, userId);
        exchange.getIn().setHeader(MESSAGE_TYPE, messageType);
        exchange.getIn().setHeader(PAYLOAD, payload);
        exchange.getIn().setHeader(ERROR_MESSAGE, errorMessage);
        exchange.getIn().setHeader(RAW_PAYLOAD, rawPayload != null ? rawPayload : "INVALID_JSON");

        exchange.getIn().setBody("INSERT INTO failed_messages (user_id, message_type, payload, status, error_message) VALUES (:?userId, :?messageType, :?rawPayload, 'FAILED', :?errorMessage)");
    }

    private void storeRawPayload(Exchange exchange) {
        String rawJson = exchange.getIn().getBody(String.class);
        exchange.getIn().setHeader(RAW_PAYLOAD, rawJson);
        validateRawJson(rawJson);
    }

    private void extractAndValidateJson(Exchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        validateJsonContent(body);
        exchange.getIn().setHeader(USER_ID, body.getOrDefault(USER_ID, UNKNOWN));
        exchange.getIn().setHeader(MESSAGE_TYPE, body.getOrDefault(MESSAGE_TYPE, UNKNOWN));
        exchange.getIn().setHeader(PAYLOAD, body.getOrDefault(PAYLOAD, UNKNOWN));
    }

    private void validateRawJson(String json) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("Received empty or null JSON payload");
        }
        if (json.contains("\u0000")) {
            throw new IllegalArgumentException("Illegal character (CTRL-CHAR, code 0) found in raw JSON");
        }
    }

    private void validateJsonContent(Map<String, Object> json) {
        Set<String> requiredFields = Set.of(USER_ID, MESSAGE_TYPE, PAYLOAD);
        for (String field : requiredFields) {
            if (!json.containsKey(field) || json.get(field) == null) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
        }
    }
}