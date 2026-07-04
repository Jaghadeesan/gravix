package com.jagha.collabflow.service;

import com.jagha.collabflow.dto.event.TaskEvent;
import com.jagha.collabflow.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void publishTaskEvent_CallsKafkaTemplate_WithCorrectTopic() {
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.TASK_STATUS_CHANGED)
                .taskId(1L)
                .newStatus(TaskStatus.IN_PROGRESS)
                .occurredAt(Instant.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        kafkaProducerService.publishTaskEvent(event);

        verify(kafkaTemplate, times(1))
                .send(eq("task-events"), eq("1"), eq(event));
    }
}