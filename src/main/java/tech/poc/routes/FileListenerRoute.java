//package tech.poc.routes;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import org.apache.camel.builder.RouteBuilder;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import tech.poc.processors.FileProcessor;
//
//@ApplicationScoped
//public class FileListenerRoute extends RouteBuilder {
//
//    private static final String ROUTE_ID_FILE = "file-listener-route";
//
//    @ConfigProperty(name = "file.watch.path", defaultValue = "target/data")
//    String filePath;
//
//    @Override
//    public void configure() {
//        from("file:" + filePath
//                + "?noop=true" // Keeps file in the folder
//                + "&idempotent=true" // Avoids reprocessing the same file
//                + "&readLock=changed" // Only process files that have finished changing
//                + "&readLockMinAge=1000") // Waits 1 second after last modification before processing
//                .routeId(ROUTE_ID_FILE)
//                .log("File received: ${header.CamelFileName}")
//                .doTry()
//                    .process(new FileProcessor())
//                    .log("Sending Processed File Content: ${body}")
//                    .to("jms:queue:fileMessages")
//                .doCatch(Exception.class)
//                    .log("ERROR processing file: ${header.CamelFileName} | Cause: ${exception.message}")
//                    .to("file:target/logs?fileName=fileProcessingErrors.log&fileExist=Append")
//                .end();
//    }
//}