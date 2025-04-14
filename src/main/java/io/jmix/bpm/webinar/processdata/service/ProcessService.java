package io.jmix.bpm.webinar.processdata.service;

import io.jmix.bpm.webinar.processdata.security.SystemAuthHelper;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component("pdt_ProcessService")
public class ProcessService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private SystemAuthHelper systemAuthHelper;

    public void runInParallel(int threads, int processesPerThread, String processId) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            int finalI = i;
            executor.submit(() -> {
                for (int j = 0; j < processesPerThread; j++) {
                    try {
                        String businessKey = "Thd." + finalI + "-proc." +  j;
                        systemAuthHelper.runWithSystemAuth(() -> runtimeService.startProcessInstanceByKey(processId, businessKey));
                    } catch (Exception e) {
                        System.err.println("Thread " + finalI + " failed at j=" + j + ": " + e.getMessage());
                        //noinspection CallToPrintStackTrace
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}