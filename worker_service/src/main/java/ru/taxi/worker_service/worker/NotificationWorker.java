package ru.taxi.worker_service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.worker_service.service.NotificationService;

@Slf4j
@RequiredArgsConstructor
public class NotificationWorker implements Runnable {

    private final NotificationService notificationService;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                notificationService.processNextTask();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Unexpected error in worker", e);
            }
        }
    }
}