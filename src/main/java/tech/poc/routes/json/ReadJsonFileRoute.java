package tech.poc.routes.json;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@ApplicationScoped
public class ReadJsonFileRoute extends RouteBuilder {

    @ConfigProperty(name = "file.json.watch.path", defaultValue = "target/input")
    String filePath;

    @Override
    public void configure() {
        from("file:" + filePath + "?noop=true&idempotent=true")
                .log("New file detected: ${header.CamelFileName}")
                .unmarshal().json() // convert json to a map
                .process(exchange -> {
                    Map<String, Object> body = exchange.getIn().getBody(Map.class);
                    exchange.getIn().setHeader("userId", body.get("userId"));
                    exchange.getIn().setHeader("messageType", body.get("messageType"));
                    exchange.getIn().setHeader("payload", body.get("payload"));
                })
                .to("direct:processJsonMessage");
    }
}
