package com.jagha.gravix.service;

import com.jagha.gravix.dto.event.TaskEvent;
import com.jagha.gravix.service.interfaces.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService implements NotificationServiceInterface {
    /**
     * @Async — runs on a separate thread from Spring's task executor.
     * The caller (SlaMonitoringService) returns immediately without
     * waiting for this method to complete.
     *
     * IMPORTANT: @Transactional does NOT propagate across @Async
     * boundaries. If you need transactional behavior here, open
     * a new transaction explicitly with @Transactional(propagation
     * = REQUIRES_NEW). This is a critical Spring gotcha.
     */
    @Async
    @Override
    public void sendTaskAssignmentNotification(TaskEvent event) {
        log.info("[NOTIFICATION] Task assigned. taskId={}, " +
                        "assigneeId={}",
                event.getTaskId(), event.getAssigneeId());
    }

    @Async
    @Override
    public void sendSlaBreachNotification(TaskEvent event) {
        log.info("[NOTIFICATION] Sending SLA breach notification. " +
                        "taskId={}, taskTitle={}",
                event.getTaskId(), event.getTaskTitle());
        // TODO Day 11: Replace with real SQS/email notification
        // For now: structured log serves as notification stub
        // This async method returns immediately to caller

        log.warn("[NOTIFICATION] SLA BREACH ALERT — " +
                        "Task '{}' (id={}) has breached its SLA deadline. " +
                        "Board: {}, Assignee: {}",
                event.getTaskTitle(),
                event.getTaskId(),
                event.getBoardId(),
                event.getAssigneeId());
    }

    @Async
    @Override
    public void sendStatusChangeNotification(TaskEvent event) {
        log.info("[NOTIFICATION] Status changed. taskId={}, " +
                        "from={}, to={}",
                event.getTaskId(),
                event.getPreviousStatus(),
                event.getNewStatus());
    }
}
