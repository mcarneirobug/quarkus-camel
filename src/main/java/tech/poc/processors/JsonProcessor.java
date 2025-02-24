package tech.poc.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import tech.poc.pojo.User;

public class JsonProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        User user = exchange.getIn().getBody(User.class);

        String greeting = "Hello, " + user.getName();

        exchange.getMessage().setBody(greeting);
    }
}