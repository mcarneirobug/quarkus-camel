package tech.poc.routes.main;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class SimpleRetryRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(3) // Retry up to 3 times
                .redeliveryDelay(1000) // Delay 1 second between retries
                .retryAttemptedLogLevel(LoggingLevel.WARN)); // Log retry attempts

        onException(RuntimeException.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000) // Delay 1 second between retries
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .handled(true) // Prevents the error from propagating further
                .log(LoggingLevel.ERROR, "Error processing message: ${body}")
                .end();

        from("direct:simple-retry")
                .log("Processing message: ${body}")
                .process(exchange -> {
                    // Simulate a failure
                    if (exchange.getIn().getBody(String.class).contains("fail")) {
                        throw new RuntimeException("Simulated failure");
                    }
                })
                .log("Message processed successfully: ${body}");
    }
}