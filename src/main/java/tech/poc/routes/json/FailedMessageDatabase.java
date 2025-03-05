package tech.poc.routes.json;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class FailedMessageDatabase extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:processJsonMessage")
                .doTry()
                    .to("bean:processingService?method=process")
                .doCatch(Exception.class)
                    .to("direct:handleFailure")
                .end();

        from("direct:handleFailure")
                .process(exchange -> {
                    var body = exchange.getIn().getBody(String.class);
                    var userId = exchange.getIn().getHeader("userId", String.class);
                    var messageType = exchange.getIn().getHeader("messageType", String.class);
                    exchange.getIn().setBody("INSERT INTO failed_messages (user_id, message_type, message) VALUES (:?userId, :?messageType, :?message)");

                    exchange.getIn().setHeader("userId", userId);
                    exchange.getIn().setHeader("messageType", messageType);
                    exchange.getIn().setHeader("message", body);
                })
                .to("jdbc:default?useHeadersAsParameters=true")
                .log("Failed message stored in the database.");
    }
}