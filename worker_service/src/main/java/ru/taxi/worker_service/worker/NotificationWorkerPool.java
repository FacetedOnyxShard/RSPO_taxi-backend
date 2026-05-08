package ru.taxi.worker_service.worker;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.taxi.worker_service.service.NotificationService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NotificationWorkerPool {

    private final NotificationService notificationService;
    private final ExecutorService executor;
    private final int poolSize;

    public NotificationWorkerPool(NotificationService notificationService,
                                  @Value("${worker.pool.size:4}") int poolSize) {
        this.notificationService = notificationService;
        this.poolSize = poolSize;
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < poolSize; i++) {
            executor.submit(new NotificationWorker(notificationService));
        }
        log.info("Started {} notification workers", poolSize);
    }

    @PreDestroy
    public void shutdownWorkers() {
        log.info("Shutting down notification workers...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Worker pool terminated.");
    }
}