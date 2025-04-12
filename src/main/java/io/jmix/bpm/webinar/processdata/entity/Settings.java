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

    @AppSettingsDefault("1L")
    @Column(name = "NEXT_ORDER_NUMBER")
    private Long nextOrderNumber;

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