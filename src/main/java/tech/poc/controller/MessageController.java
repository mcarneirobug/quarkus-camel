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

@Path("/messages")
@ApplicationScoped
public class MessageController {

    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;

    public MessageController(ProducerTemplate producerTemplate, ObjectMapper objectMapper) {
        this.producerTemplate = producerTemplate;
        this.objectMapper = objectMapper;
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
    @Path("/error")
    public Response triggerErrorRoute() {
        producerTemplate.sendBody("direct:handleErrors", "Testing error.");
        return Response.ok("Error simulated.")
                .build();
    }
}