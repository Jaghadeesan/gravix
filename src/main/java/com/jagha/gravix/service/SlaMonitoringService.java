package com.jagha.gravix.service;

import com.jagha.gravix.config.SlaProperties;
import com.jagha.gravix.dto.event.TaskEvent;
import com.jagha.gravix.dto.sla.SlaTaskItem;
import com.jagha.gravix.dto.sla.SlaTaskItem.SlaStatus;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.repository.TaskRepository;
import com.jagha.gravix.service.interfaces.EventPublisherInterface;
import com.jagha.gravix.service.interfaces.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlaMonitoringService {
    private final TaskRepository taskRepository;
    private final SlaProperties slaProperties;
    private final EventPublisherInterface eventPublisher;
    private final NotificationServiceInterface notificationService;

    /**
     * Scheduled SLA breach detection.
     * Runs every minute (configurable via properties).
     *
     * Algorithm:
     * 1. Load all active (non-DONE) tasks with due dates
     * 2. Build a min-heap (PriorityQueue) ordered by due date
     * 3. Pop tasks from heap — earliest due date first
     * 4. Classify each task: ON_TRACK, WARNING, or BREACHED
     * 5. Publish Kafka event and trigger notification for breaches
     *
     * Time complexity: O(N log N) — N tasks, heap operations O(log N)
     * Space complexity: O(N) — all tasks in heap simultaneously
     */
    @Scheduled(fixedRateString = "${gravix.sla.breach-check-interval-ms:60000}")
    public void checkSlaBreaches() {
        log.info("[SLA_MONITOR] Starting SLA breach check");

        List<Task> activeTasks = taskRepository.findActivetasksWithDueDate();

        if (activeTasks.isEmpty()) {
            log.info("[SLA_MONITOR] No active tasks with due dates");
            return;
        }

        // Build min-heap — earliest due date has highest priority
        PriorityQueue<SlaTaskItem> slaHeap = new PriorityQueue<>(activeTasks.size());

        for(Task task : activeTasks) {
            SlaTaskItem item = SlaTaskItem.builder()
                    .taskId(task.getId())
                    .taskTitle(task.getTitle())
                    .boardId(task.getBoard().getId())
                    .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                    .dueDate(task.getDueDate())
                    .status(task.getStatus())
                    .build();
            slaHeap.offer(item);
        }

        int breachCount = 0;
        int warningCount = 0;

        // Process heap — handles most urgent tasks first
        while(!slaHeap.isEmpty()) {
            SlaTaskItem item = slaHeap.poll();
            SlaStatus status = classifySlaStatus(item.getDueDate());
            item.setSlaStatus(status);

            if (status == SlaStatus.BREACHED) {
                breachCount++;
                handleBreach(item);
            } else if (status == SlaStatus.WARNING) {
                warningCount++;
                handleWarning(item);
            } else {
                // ON_TRACK — since heap is ordered by due date,
                // all remaining tasks are also on track
                // We can break early as optimization
                break;
            }
        }
        log.info("[SLA_MONITOR] Check complete. " +
                        "breaches={}, warnings={}, totalChecked={}",
                breachCount, warningCount, activeTasks.size());
    }

    private void handleWarning(SlaTaskItem item) {
        log.warn("[SLA_WARNING] Task approaching SLA breach. " +
                        "taskId={}, title={}, dueDate={}",
                item.getTaskId(), item.getTaskTitle(), item.getDueDate());
    }

    private void handleBreach(SlaTaskItem item) {
        log.warn("[SLA_BREACH] Task breached SLA. " +
                        "taskId={}, title={}, dueDate={}",
                item.getTaskId(), item.getTaskTitle(), item.getDueDate());

        // Publish Kafka event
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.SLA_BREACHED)
                .taskId(item.getTaskId())
                .taskTitle(item.getTaskTitle())
                .boardId(item.getBoardId())
                .assigneeId(item.getAssigneeId())
                .occurredAt(Instant.now())
                .build();

        eventPublisher.publishTaskEvent(event);

        // Async notification
        notificationService.sendSlaBreachNotification(event);
    }

    private SlaStatus classifySlaStatus(Instant dueDate) {
        if (dueDate == null) return SlaStatus.ON_TRACK;

        Instant now = Instant.now();

        if (now.isAfter(dueDate)) {
            return SlaStatus.BREACHED;
        }

        long hoursUntilDue = ChronoUnit.HOURS.between(now, dueDate);

        if (hoursUntilDue <= slaProperties.getCriticalThresholdHours()) {
            return SlaStatus.WARNING;
        }

        return SlaStatus.ON_TRACK;
    }
}
