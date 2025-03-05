package tech.poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.camel.ProducerTemplate;
import tech.poc.pojo.User;
import tech.poc.routes.main.DirectResendTrigger;
import tech.poc.routes.main.PartialFailureTrigger;
import tech.poc.routes.main.TransientErrorTrigger;

import java.util.logging.Logger;

@Path("/messages")
@ApplicationScoped
public class MessageController {

    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private final PartialFailureTrigger partialFailureTrigger;
    private final TransientErrorTrigger transientErrorTrigger;
    private final DirectResendTrigger directResendTrigger;

    public MessageController(ProducerTemplate producerTemplate, ObjectMapper objectMapper, PartialFailureTrigger partialFailureTrigger, TransientErrorTrigger transientErrorTrigger, DirectResendTrigger directResendTrigger) {
        this.producerTemplate = producerTemplate;
        this.objectMapper = objectMapper;
        this.partialFailureTrigger = partialFailureTrigger;
        this.transientErrorTrigger = transientErrorTrigger;
        this.directResendTrigger = directResendTrigger;
    }

    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendJsonMessage(User user) {
        try {
            String jsonBody = objectMapper.writeValueAsString(user);
            producerTemplate.sendBody("direct:processJson", jsonBody);
            return Response.ok("Message sent to ActiveMQ.")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error converting User to JSON: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/failRoutingAPI")
    public String failRoutingAPI() {
        producerTemplate.sendBody("direct:handleTransientError", "Simulated Failure");
        return "Routing API failure simulated!";
    }

    @POST
    @Path("/retryFailed")
    public String retryFailedMessages() {
        producerTemplate.sendBody("timer:reprocess", "Trigger manual retry");
        return "Retry triggered!";
    }

    @POST
    @Path("/error")
    public Response triggerErrorRoute() {
        producerTemplate.sendBody("direct:handleErrors", "Testing error.");
        return Response.ok("Error simulated.")
                .build();
    }

    @POST
    @Path("/partialRoutingAPI")
    public String partialFailRoutingAPI() {
        partialFailureTrigger.triggerRoute();
        return "Routing API partial failure simulated!";
    }

    @POST
    @Path("/failTransientAPI")
    public String failTransientAPI() {
        transientErrorTrigger.triggerRoute();
        return "Routing API transient failure simulated!";
    }

    @POST
    @Path("/failDirectResendAPI")
    public String failDirectResendAPI() {
        directResendTrigger.sendMessage("Test Message");
        return "Routing API redirect resend failure simulated!";
    }
}