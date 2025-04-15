package io.jmix.bpm.webinar.processdata.entity;

import io.jmix.core.metamodel.datatype.EnumClass;
import org.springframework.lang.Nullable;


public enum InventoryOperation implements EnumClass<Integer> {

    RESERVATION(10),
    CANCEL_RESERVATION(20),
    DELIVERY(30),
    PRODUCTION(40);

    private final Integer id;


    InventoryOperation(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static InventoryOperation fromId(Integer id) {
        for (InventoryOperation at : InventoryOperation.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}