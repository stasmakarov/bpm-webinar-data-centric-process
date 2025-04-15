package io.jmix.bpm.webinar.processdata.rabbit;

import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitService {
    private static final Logger log = LoggerFactory.getLogger(RabbitService.class);

    private final RabbitListenerEndpointRegistry endpointRegistry;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate rabbitTemplate;
    private final AppSettings appSettings;
    private final ApplicationContext applicationContext;

    public RabbitService(ConnectionFactory connectionFactory,
                         RabbitListenerEndpointRegistry endpointRegistry,
                         RabbitTemplate rabbitTemplate,
                         AppSettings appSettings,
                         ApplicationContext applicationContext) {
        this.connectionFactory = connectionFactory;
        this.endpointRegistry = endpointRegistry;
        this.rabbitTemplate = rabbitTemplate;
        this.appSettings = appSettings;
        this.applicationContext = applicationContext;
    }

    public boolean isRabbitAvailable() {
        try {
            RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
            String queueName = appSettings.load(Settings.class).getInventoryQueue();
            rabbitAdmin.getQueueProperties(queueName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean startAllContainers() {
        // Get all beans of type MessageListenerContainer
        Map<String, SimpleMessageListenerContainer> containers = applicationContext.getBeansOfType(SimpleMessageListenerContainer.class);
        boolean allStarted = true;

        for (Map.Entry<String, SimpleMessageListenerContainer> entry : containers.entrySet()) {
            String name = entry.getKey();
            SimpleMessageListenerContainer container = entry.getValue();

            String[] queueNames = container.getQueueNames();
            for (String queueName : queueNames) {
                createQueueIfNotExists(queueName);
            }


            // Start the container if it's not running
            if (!container.isRunning()) {
                try {
                    container.start();
                    log.info("✅ Container {} started successfully.", name);
                } catch (Exception e) {
                    log.info("❌ Failed to start container {}: {}", name, e.getMessage());
                    allStarted = false;
                }
            } else {
                log.info("⚠\uFE0F Container {} is already running.", name);
            }
        }

        // Return true if all containers are running, false otherwise
        return allStarted;
    }

    public boolean stopAllContainers() {
        // Get all beans of type MessageListenerContainer
        Map<String, MessageListenerContainer> containers = applicationContext.getBeansOfType(MessageListenerContainer.class);
        boolean allStopped = true;

        for (Map.Entry<String, MessageListenerContainer> entry : containers.entrySet()) {
            String name = entry.getKey();
            MessageListenerContainer container = entry.getValue();
            System.out.println("📦 Container bean name: " + name + ", running: " + container.isRunning());

            // Stop the container if it's running
            if (container.isRunning()) {
                try {
                    container.stop();
                    System.out.println("✅ Container " + name + " stopped successfully.");
                } catch (Exception e) {
                    System.out.println("❌ Failed to stop container " + name + ": " + e.getMessage());
                    allStopped = false;
                }
            } else {
                System.out.println("⚠️ Container " + name + " is not running.");
            }
        }

        // Return true if all containers are stopped, false otherwise
        return allStopped;
    }


    public void purgeQueue(String queueName) {
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(queueName);
            return null;
        });
    }

    public void createQueueIfNotExists(String queueName) {
        ConnectionFactory connectionFactory = applicationContext.getBean(ConnectionFactory.class);
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        Queue queue = new Queue(queueName, true);
        try {
            admin.declareQueue(queue);
            log.info("Queue {} declared successfully.", queueName);
        } catch (Exception e) {
            log.error("Error declaring queue {}: {}", queueName, e.getMessage());
        }
    }

    public boolean isRabbitRunning() {
        return endpointRegistry.getListenerContainers()
                .stream()
                .allMatch(Lifecycle::isRunning);
    }

}
