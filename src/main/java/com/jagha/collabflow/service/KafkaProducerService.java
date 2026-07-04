package com.jagha.collabflow.service;

import com.jagha.collabflow.config.KafkaTopics;
import com.jagha.collabflow.dto.event.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publicTaskEvent(TaskEvent event) {
        // Use taskId as the message key — ensures all events for
        // the same task go to the same partition (ordering guarantee)
        String key = String.valueOf(event.getTaskId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaTopics.TASK_EVENTS, key, event);

        future.whenComplete((result, ex) -> {
            if(ex != null) {
                log.error("[KAFKA] Failed to publish task event. " +
                        "eventType={}, taskId={}, error={}",
                        event.getEventType(),
                        event.getTaskId(),
                        ex.getMessage());
            } else {
                log.info("[KAFKA] Task published successfully. " +
                        "eventType={}, taskId={}, partitionId={}, offset={}",
                        event.getEventType(),
                        event.getTaskId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

}
