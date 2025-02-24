package tech.poc.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcessor implements Processor {

    private static final String HEADER_FILENAME = "CamelFileName";
    private static final String HEADER_FILEPATH = "CamelFileAbsolutePath";
    private static final String PROCESSED_CONTENT = "Processed File: %s\n Size: %d bytes\n Last Modified: %s\n Content:\n%s";

    @Override
    public void process(Exchange exchange) throws IOException {
        String fileContent = exchange.getIn().getBody(String.class);
        String fileName = exchange.getIn().getHeader(HEADER_FILENAME, String.class);
        String filePath = exchange.getIn().getHeader(HEADER_FILEPATH, String.class);

        if (fileContent == null || fileContent.trim().isEmpty() || fileName == null || filePath == null) throw new IllegalArgumentException("File " + fileName + " is empty!");

        Path path = Paths.get(filePath);

        long fileSize = Files.size(path);
        String modifiedTime = String.valueOf(Files.getLastModifiedTime(path));

        final var processedMessage = String.format(PROCESSED_CONTENT, fileName, fileSize, modifiedTime, fileContent);

        exchange.getIn().setHeader("FileSize", fileSize);
        exchange.getIn().setHeader("ModifiedTime", modifiedTime);

        exchange.getMessage().setBody(processedMessage);
    }
}