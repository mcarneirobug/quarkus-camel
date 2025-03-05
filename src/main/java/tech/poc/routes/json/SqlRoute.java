package tech.poc.routes.json;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class SqlRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:insertMessage")
                .setHeader("userId", constant("123")) // Set the userId header
                .setHeader("messageType", constant("notification")) // Set the messageType header
                .setHeader("message", constant("Hello, World!")) // Set the message header
                .setBody(constant("INSERT INTO failed_messages (user_id, message_type, message) VALUES (:?userId, :?messageType, :?message)"))
                .to("jdbc:default?useHeadersAsParameters=true")
                .log("Message inserted into the database.");
    }
}
