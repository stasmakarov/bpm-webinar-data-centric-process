package io.jmix.bpm.webinar.processdata.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class InventoryMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(InventoryMessageProducer.class);

    @Autowired
    @Qualifier("inventoryQueue")
    private String inventoryQueueName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendInventoryMessage(InventoryRequest inventoryRequest) {

        try {
            rabbitTemplate.convertAndSend(inventoryQueueName, inventoryRequest);
            log.info("Sent inventory message for process '{}'", inventoryRequest.getBusinessKey());
        } catch (AmqpException e) {
            log.error("Failed to send inventory message", e);
            throw e;
        }
    }
}