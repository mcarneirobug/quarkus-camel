//package tech.poc;
//
//import io.quarkus.runtime.StartupEvent;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.inject.Inject;
//import org.apache.camel.ProducerTemplate;
//import tech.poc.routes.json.SqlRouteTrigger;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//@ApplicationScoped
//public class MainApp {
//
//    @Inject
//    SqlRouteTrigger sqlRouteTrigger;
//    @Inject
//    DataSource dataSource;
//
//    void onStart(@Observes StartupEvent ev) {
//        try (Connection conn = dataSource.getConnection();
//             Statement stmt = conn.createStatement()) {
//            // Create the failed_messages table
//            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS failed_messages (" +
//                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
//                    "user_id VARCHAR(255), " +
//                    "message_type VARCHAR(255), " +
//                    "message TEXT)");
//            System.out.println("Table 'failed_messages' created successfully.");
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to create table 'failed_messages'", e);
//        }
//
//        start();
//    }
//
//    public void start() {
//        String jsonMessage = "{\"name\": \"John Doe\"}";
////        producerTemplate.sendBody("direct:processJson", jsonMessage);
////        producerTemplate.sendBody("direct:processActiveMQ", jsonMessage);
//
////        producerTemplate.sendBody("direct:handleErrors", "Test error");
//
//        sqlRouteTrigger.triggerRoute();
//
//        System.out.println("Application started and routes triggered!");
//    }
//}