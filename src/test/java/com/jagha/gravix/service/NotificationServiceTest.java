package com.jagha.gravix.service;

import com.jagha.gravix.dto.event.TaskEvent;
import com.jagha.gravix.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
    }

    private TaskEvent buildEvent(String type) {
        return TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(type)
                .taskId(1L)
                .taskTitle("Test Task")
                .boardId(1L)
                .assigneeId(2L)
                .previousStatus(TaskStatus.TODO)
                .newStatus(TaskStatus.IN_PROGRESS)
                .occurredAt(Instant.now())
                .build();
    }

    @Test
    void sendSlaBreachNotification_ValidEvent_DoesNotThrow() {
        TaskEvent event = buildEvent(TaskEvent.SLA_BREACHED);
        assertDoesNotThrow(() ->
                notificationService.sendSlaBreachNotification(event));
    }

    @Test
    void sendSlaBreachNotification_NullAssignee_DoesNotThrow() {
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.SLA_BREACHED)
                .taskId(1L)
                .taskTitle("No Assignee Task")
                .boardId(1L)
                .assigneeId(null)
                .occurredAt(Instant.now())
                .build();

        assertDoesNotThrow(() ->
                notificationService.sendSlaBreachNotification(event));
    }

    @Test
    void sendTaskAssignmentNotification_ValidEvent_DoesNotThrow() {
        TaskEvent event = buildEvent(TaskEvent.TASK_ASSIGNED);
        assertDoesNotThrow(() ->
                notificationService
                        .sendTaskAssignmentNotification(event));
    }

    @Test
    void sendStatusChangeNotification_ValidEvent_DoesNotThrow() {
        TaskEvent event = buildEvent(
                TaskEvent.TASK_STATUS_CHANGED);
        assertDoesNotThrow(() ->
                notificationService
                        .sendStatusChangeNotification(event));
    }

    @Test
    void sendStatusChangeNotification_NullStatuses_DoesNotThrow() {
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.TASK_STATUS_CHANGED)
                .taskId(1L)
                .taskTitle("Test Task")
                .boardId(1L)
                .previousStatus(null)
                .newStatus(null)
                .occurredAt(Instant.now())
                .build();

        assertDoesNotThrow(() ->
                notificationService
                        .sendStatusChangeNotification(event));
    }

    @Test
    void sendSlaBreachNotification_LongTaskTitle_DoesNotThrow() {
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.SLA_BREACHED)
                .taskId(99L)
                .taskTitle("A".repeat(200))
                .boardId(1L)
                .occurredAt(Instant.now())
                .build();

        assertDoesNotThrow(() ->
                notificationService.sendSlaBreachNotification(event));
    }
}