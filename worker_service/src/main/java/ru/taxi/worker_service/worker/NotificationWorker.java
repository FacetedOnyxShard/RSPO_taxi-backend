package ru.taxi.worker_service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.worker_service.model.NotificationStatus;
import ru.taxi.worker_service.model.NotificationTask;
import ru.taxi.worker_service.repository.NotificationRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class NotificationWorker implements Runnable {
    private final NotificationRepository repository;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Optional<NotificationTask> taskOpt = repository.claimPendingTask();
                if (taskOpt.isPresent()) {
                    processTask(taskOpt.get());
                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Unexpected error in worker", e);
            }
        }
    }

    private void processTask(NotificationTask task) {
        try {
            log.info("Sending notification: id={}, tripId={}, message={}",
                    task.getId(), task.getTripId(), task.getMessage());
            Thread.sleep(500);

            repository.updateStatus(task.getId(), NotificationStatus.SENT);
            log.info("Notification sent: id={}", task.getId());
        } catch (Exception e) {
            log.error("Failed to send notification id={}, retryCount={}",
                    task.getId(), task.getRetryCount(), e);
            repository.incrementRetry(task.getId());

            if (task.getRetryCount() + 1 >= 3) {
                repository.updateStatus(task.getId(), NotificationStatus.FAILED);
                log.info("Notification failed after max retries: id={}", task.getId());
            } else {
                repository.updateStatus(task.getId(), NotificationStatus.PENDING);
                log.info("Notification returned to PENDING for retry: id={}", task.getId());
            }
        }
    }
}