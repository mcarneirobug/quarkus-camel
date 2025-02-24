package tech.poc.routes;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import tech.poc.pojo.User;
import tech.poc.processors.JsonProcessor;

@ApplicationScoped
public class JsonProcessingRoute extends RouteBuilder {

    private static final String ROUTE_ID_JSON = "json-processing-route";

    @Override
    public void configure() {
        from("direct:processJson")
                .routeId(ROUTE_ID_JSON)
                .log("Received JSON: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, User.class) // Unmarshal JSON to User object
                .process(new JsonProcessor())
                .to("jms:queue:processedMessages");
    }
}
