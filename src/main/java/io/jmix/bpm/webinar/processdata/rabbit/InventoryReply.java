package io.jmix.bpm.webinar.processdata.rabbit;

public class InventoryReply {

    private String orderId;
    private String businessKey;
    private boolean isReserved;
    private String errorMessage;
    private String processInstanceId;

    public InventoryReply(String orderId,
                          String businessKey,
                          boolean isReserved,
                          String errorMessage,
                          String processInstanceId) {
        this.orderId = orderId;
        this.businessKey = businessKey;
        this.isReserved = isReserved;
        this.errorMessage = errorMessage;
        this.processInstanceId = processInstanceId;
    }

    public InventoryReply() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public String isError() {
        return errorMessage;
    }

    public void setError(String error) {
        errorMessage = error;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}
