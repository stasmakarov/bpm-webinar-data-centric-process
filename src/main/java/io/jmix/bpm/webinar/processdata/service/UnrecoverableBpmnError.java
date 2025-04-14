package io.jmix.bpm.webinar.processdata.service;

import org.flowable.engine.delegate.BpmnError;

public class UnrecoverableBpmnError extends RuntimeException {
    private final BpmnError bpmnError;

    public UnrecoverableBpmnError(BpmnError bpmnError) {
        this.bpmnError = bpmnError;
    }

    public BpmnError getBpmnError() {
        return bpmnError;
    }
}
