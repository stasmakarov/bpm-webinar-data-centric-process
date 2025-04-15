package io.jmix.bpm.webinar.processdata.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.bpm.webinar.processdata.security.SystemAuthHelper;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class InventoryReplyHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryReplyHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private SystemAuthHelper systemAuthHelper;

    @RabbitListener(id = "inventoryReplyListener", queues = "#{@replyQueue}", autoStartup = "false")
    public void handleReply(@Payload InventoryReply payload) {
        if (payload == null) {
            log.warn("Received null payload in inventory reply");
            return;
        }

        try {
            String processInstanceId = payload.getProcessInstanceId();

            if (processInstanceId == null) {
                log.error("Missing processInstanceId in reply");
                return;
            }

            String messageName;
            String messageEventId;

            if (payload.isReserved()) {
                messageName = "Reservation success message";
                messageEventId = "success-message-event";
            } else {
                messageName = "Inventory failure message";
                messageEventId = "failure-message-event";
            }

            Execution execution = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstanceId)
                    .activityId(messageEventId)
                    .singleResult();

            if (execution != null) {
                systemAuthHelper.runWithSystemAuth(() -> {
                    runtimeService.messageEventReceived(messageName, execution.getId());
                    return null;
                });
            }

            log.info("Message '{}' to process {}", messageName, processInstanceId);

        } catch (Exception e) {
            log.error("Failed to handle inventory reply", e);
        }
    }
}

