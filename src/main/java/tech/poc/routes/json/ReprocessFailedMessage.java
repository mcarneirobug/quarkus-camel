//package tech.poc.routes.json;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import org.apache.camel.builder.RouteBuilder;
//
//import java.util.Map;
//
//@ApplicationScoped
//public class ReprocessFailedMessage extends RouteBuilder {
//    @Override
//    public void configure() {
//        from("timer:reprocess?period=60000") // Poll every 60 seconds
//                .setBody(constant("SELECT * FROM failed_messages"))
//                .to("jdbc:default")
//                .split(body()) // Split the result set
//                .process(exchange -> {
//                    // Extract message details from the database
//                    var row = exchange.getIn().getBody(Map.class);
//                    exchange.getIn().setBody(row.get("message"));
//                    exchange.getIn().setHeader("userId", row.get("user_id"));
//                    exchange.getIn().setHeader("messageType", row.get("message_type"));
//                    exchange.getIn().setHeader("id", row.get("id")); // Store the ID for deletion
//                })
//                .to("direct:processJsonMessage")
//                .process(exchange -> {
//                    // Delete the successfully reprocessed message from the database
//                    var id = exchange.getIn().getHeader("id", String.class);
//                    exchange.getIn().setBody("DELETE FROM failed_messages WHERE id = :?id");
//                    exchange.getIn().setHeader("id", id); // Set the ID as a named parameter
//                })
//                .to("jdbc:default?useHeadersAsParameters=true") // Use JDBC component
//                .log("Successfully reprocessed and deleted message with ID: ${header.id}");
//    }
//}
