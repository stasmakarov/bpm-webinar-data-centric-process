package io.jmix.bpm.webinar.processdata.rabbit;

import io.jmix.bpm.webinar.processdata.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class InventoryMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryMessageHandler.class);

    @Autowired
    @Qualifier("replyQueue")
    private String replyQueueName;

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(id = "inventoryMessageListener", queues = "#{@inventoryQueue}", autoStartup = "false")
    public void handleMessage(@Payload InventoryRequest inventoryRequest) {
        if (inventoryRequest == null) {
            log.error("Received null InventoryRequest");
            return;
        }

        try {
            InventoryReply inventoryReply = inventoryService.proceedRequest(inventoryRequest);
            log.info("Inventory request processed. Response = {}", inventoryReply);

            sendResponse(inventoryReply);

        } catch (Exception e) {
            log.error("Error while processing inventory request", e);
            sendResponse(new InventoryReply());
        }
    }

    public void sendResponse(InventoryReply inventoryReply) {
        try {
            rabbitTemplate.convertAndSend(replyQueueName, inventoryReply);
            log.info("Sent inventory response to queue {}", replyQueueName);
        } catch (AmqpException e) {
            log.error("Error sending inventory response to queue", e);
        }
    }
}
