package com.jagha.collabflow.service.interfaces;

import com.jagha.collabflow.dto.event.TaskEvent;

public interface EventPublisherInterface {
    void publishTaskEvent(TaskEvent event);
}