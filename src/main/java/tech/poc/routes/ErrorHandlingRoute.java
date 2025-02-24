package tech.poc.routes;

import org.apache.camel.builder.RouteBuilder;

public class ErrorHandlingRoute extends RouteBuilder {

    public static final String ROUTE_ID_ERROR = "error-handling-route";

    @Override
    public void configure() {
        from("direct:handleErrors")
                .routeId(ROUTE_ID_ERROR)
                .doTry()
                .process(exchange -> {
                    throw new IllegalArgumentException("Simulated error");
                })
                .doCatch(IllegalArgumentException.class)
                .log("Caught exception: ${exception.message}")
                .setBody(constant("Error handled gracefully"))
                .end()
                .to("log:errorHandlingRoute");
    }
}
