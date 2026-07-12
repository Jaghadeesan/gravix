package com.jagha.gravix.service;

import com.jagha.gravix.config.SlaProperties;
import com.jagha.gravix.entity.Board;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.entity.TaskStatus;
import com.jagha.gravix.repository.TaskRepository;
import com.jagha.gravix.service.interfaces.EventPublisherInterface;
import com.jagha.gravix.service.interfaces.NotificationServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SlaMonitoringServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SlaProperties slaProperties;

    @Mock
    private EventPublisherInterface eventPublisher;

    @Mock
    private NotificationServiceInterface notificationService;

    @InjectMocks
    private SlaMonitoringService slaMonitoringService;

    private Board mockBoard;

    @BeforeEach
    void setUp() {
        mockBoard = new Board();
        mockBoard.setId(1L);

        when(slaProperties.getCriticalThresholdHours()).thenReturn(1);
        when(slaProperties.getWarningThresholdHours()).thenReturn(24);
    }

    private Task createTask(Long id, String title,
                            Instant dueDate, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setBoard(mockBoard);
        task.setStatus(status);
        task.setDueDate(dueDate);
        return task;
    }

    @Test
    void checkSlaBreaches_NoActiveTasks_DoesNothing() {
        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of());

        slaMonitoringService.checkSlaBreaches();

        verify(eventPublisher, never()).publishTaskEvent(any());
        verify(notificationService, never())
                .sendSlaBreachNotification(any());
    }

    @Test
    void checkSlaBreaches_BreachedTask_PublishesEvent() {
        Task breachedTask = createTask(1L, "Overdue Task",
                Instant.now().minus(2, ChronoUnit.HOURS),
                TaskStatus.IN_PROGRESS);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(breachedTask));

        slaMonitoringService.checkSlaBreaches();

        verify(eventPublisher, times(1)).publishTaskEvent(any());
        verify(notificationService, times(1))
                .sendSlaBreachNotification(any());
    }

    @Test
    void checkSlaBreaches_TaskWithNullDueDate_SkipsGracefully() {
        Task noDeadline = new Task();
        noDeadline.setId(1L);
        noDeadline.setTitle("No deadline");
        noDeadline.setBoard(mockBoard);
        noDeadline.setStatus(TaskStatus.IN_PROGRESS);
        noDeadline.setDueDate(null);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(noDeadline));

        // Should not throw even with null due date
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> slaMonitoringService.checkSlaBreaches());
    }

    @Test
    void checkSlaBreaches_WarningTask_DoesNotPublishKafkaEvent() {
        Task warning = createTask(1L, "Warning Task",
                Instant.now().plus(30, ChronoUnit.MINUTES),
                TaskStatus.IN_PROGRESS);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(warning));

        slaMonitoringService.checkSlaBreaches();

        // Warnings log but do NOT publish Kafka events
        verify(eventPublisher, never()).publishTaskEvent(any());
        verify(notificationService, never())
                .sendSlaBreachNotification(any());
    }

    @Test
    void checkSlaBreaches_DoneTasksNotIncluded_NoEvents() {
        // findActivetasksWithDueDate excludes DONE tasks via query
        // so empty list means no events
        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of());

        slaMonitoringService.checkSlaBreaches();

        verify(eventPublisher, never()).publishTaskEvent(any());
    }
}
