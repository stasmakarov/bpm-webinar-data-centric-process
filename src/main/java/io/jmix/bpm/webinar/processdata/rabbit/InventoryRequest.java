package io.jmix.bpm.webinar.processdata.rabbit;

public class InventoryRequest {

    private String productId;
    private String orderId;
    private String businessKey;
    private long quantity;
    private int operationId;
    private String processInstanceId;

    public InventoryRequest(String productId,
                            String orderId,
                            String businessKey,
                            long quantity,
                            int operationId,
                            String processInstanceId) {
        this.productId = productId;
        this.orderId = orderId;
        this.businessKey = businessKey;
        this.quantity = quantity;
        this.operationId = operationId;
        this.processInstanceId = processInstanceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}
