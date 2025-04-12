package io.jmix.bpm.webinar.processdata.service;

import io.jmix.bpm.webinar.processdata.repository.OrderRepository;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("pdt_ResetService")
public class ResetService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;


    public void resetApp() {
        deleteAllProcessInstances();
        orderService.clear();
        inventoryService.init();
    }

    public void deleteAllProcessInstances() {
        deleteCompletedProcessInstances();
        terminateActiveProcessInstances();
    }

    @Transactional
    private void deleteCompletedProcessInstances() {
        int batchSize = 50;

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .finished(); // Only get completed processes

        List<String> instanceIds;
        do {
            instanceIds = query.listPage(0, batchSize)
                    .stream()
                    .map(HistoricProcessInstance::getId)
                    .toList();

            for (String instanceId : instanceIds) {
                deleteInstanceWithRetry(instanceId); // Retry deletion if needed
            }
        } while (!instanceIds.isEmpty());
    }
    private static final int MAX_RETRIES = 3;

    private void deleteInstanceWithRetry(String instanceId) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                historyService.deleteHistoricProcessInstance(instanceId);
                return;
            } catch (Exception e) {
                attempt++;
                System.out.println("Retrying deletion: " + instanceId + " (Attempt " + attempt + ")");
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void terminateActiveProcessInstances() {
        ProcessInstanceQuery activeQuery = runtimeService.createProcessInstanceQuery();
        activeQuery.list()
                .forEach(instance -> runtimeService.deleteProcessInstance(
                        instance.getId(),
                        "Bulk deletion requested"
                ));
    }

    private void initProducts() {

    }

}