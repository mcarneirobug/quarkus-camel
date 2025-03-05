package tech.poc.routes.json;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("processingService")
public class ProcessingService {
    public void process(String message) throws Exception {
        // Simulate different processing scenarios
        if (message.contains("fail")) {
            throw new RuntimeException("Simulated processing failure");
        } else if (message.contains("retry")) {
            throw new RuntimeException("Simulated retryable failure");
        }
        // Successful processing
        System.out.println("Message processed successfully: " + message);
    }
}