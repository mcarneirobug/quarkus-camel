package tech.poc.config;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Singleton;
import jakarta.jms.ConnectionFactory;
import jakarta.ws.rs.Produces;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

import tech.poc.aspect.ActiveMQClassic;

@ApplicationScoped
public class ActiveMQConfig {

    @Produces
    @Singleton
    @ActiveMQClassic
    public ConnectionFactory activeMQClassicConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");
        factory.setTrustAllPackages(true);
        return factory;
    }

    @Produces
    @Singleton
    @ActiveMQClassic
    public JmsComponent activeMQClassicComponent(@ActiveMQClassic ConnectionFactory connectionFactory) {
        JmsComponent jms = new JmsComponent();
        jms.setConnectionFactory(connectionFactory);
        return jms;
    }

//    @Produces
//    @Singleton
//    @ActiveMQArtemis
//    public ConnectionFactory activeMQArtemisConnectionFactory(ArtemisRuntimeConfig artemisConfig) {
//        return new ActiveMQJMSConnectionFactory(
//                artemisConfig.url().orElse("tcp://localhost:61617"),
//                artemisConfig.username().orElse("test"),
//                artemisConfig.password().orElse("test"));
//    }
//
//    @Produces
//    @Singleton
//    @ActiveMQArtemis
//    public JmsComponent activeMQArtemisComponent(@ActiveMQArtemis ConnectionFactory connectionFactory) {
//        JmsComponent jms = new JmsComponent();
//        jms.setConnectionFactory(connectionFactory);
//        return jms;
//    }
}