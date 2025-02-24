//package tech.poc;
//
//import io.quarkus.runtime.StartupEvent;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.inject.Inject;
//import org.apache.camel.ProducerTemplate;
//
//@ApplicationScoped
//public class MainApp {
//
//    @Inject
//    ProducerTemplate producerTemplate;
//
//    void onStart(@Observes StartupEvent ev) {
//        start();
//    }
//
//    public void start() {
//        String jsonMessage = "{\"name\": \"John Doe\"}";
//        producerTemplate.sendBody("direct:processJson", jsonMessage);
//        producerTemplate.sendBody("direct:processActiveMQ", jsonMessage);
//
//        producerTemplate.sendBody("direct:handleErrors", "Test error");
//
//        System.out.println("Application started and routes triggered!");
//    }
//}