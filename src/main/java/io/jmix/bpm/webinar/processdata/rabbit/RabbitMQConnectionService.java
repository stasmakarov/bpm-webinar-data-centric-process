package io.jmix.bpm.webinar.processdata.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConnectionService {
    private ConnectionFactory connectionFactory;
    private boolean isInitialized = false;

    public ConnectionFactory getConnectionFactory() {
        if (!isInitialized) {
            connectionFactory = new CachingConnectionFactory("localhost");
            ((CachingConnectionFactory) connectionFactory).setUsername("guest");
            ((CachingConnectionFactory) connectionFactory).setPassword("guest");
            isInitialized = true;
        }
        return connectionFactory;
    }}
