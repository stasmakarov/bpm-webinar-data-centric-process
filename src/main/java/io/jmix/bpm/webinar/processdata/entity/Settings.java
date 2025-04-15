package io.jmix.bpm.webinar.processdata.entity;

import io.jmix.appsettings.defaults.AppSettingsDefault;
import io.jmix.appsettings.entity.AppSettingsEntity;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@JmixEntity
@Table(name = "PDT_SETTINGS")
@Entity(name = "pdt_Settings")
public class Settings extends AppSettingsEntity {
    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @AppSettingsDefault("10")
    @Column(name = "MAX_QUANTITY")
    private Long maxQuantity;

    @AppSettingsDefault("100")
    @Column(name = "IN_STOCK")
    private Long inStock;

    @AppSettingsDefault("1")
    @Column(name = "NEXT_ORDER_NUMBER")
    private Long nextOrderNumber;

    @AppSettingsDefault("3")
    @Column(name = "THREADS")
    private Integer threads;

    @AppSettingsDefault("5")
    @Column(name = "PROCESS_PER_THREAD")
    private Integer processPerThread;

    @AppSettingsDefault("inventoryMessageQueue")
    @Column(name = "INVENTORY_MESSAGE_QUEUE")
    private String inventoryQueue;

    @AppSettingsDefault("inventoryReplyQueue")
    @Column(name = "INVENTORY_REPLY_QUEUE")
    private String replyQueue;

    public String getReplyQueue() {
        return replyQueue;
    }

    public void setReplyQueue(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public String getInventoryQueue() {
        return inventoryQueue;
    }

    public void setInventoryQueue(String inventoryQueue) {
        this.inventoryQueue = inventoryQueue;
    }

    public Integer getProcessPerThread() {
        return processPerThread;
    }

    public void setProcessPerThread(Integer processPerThread) {
        this.processPerThread = processPerThread;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Long getInStock() {
        return inStock;
    }

    public void setInStock(Long inStock) {
        this.inStock = inStock;
    }

    public Long getNextOrderNumber() {
        return nextOrderNumber;
    }

    public void setNextOrderNumber(Long nextOrderNumber) {
        this.nextOrderNumber = nextOrderNumber;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}