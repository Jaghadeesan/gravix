package com.jagha.gravix.service.interfaces;

import com.jagha.gravix.dto.event.TaskEvent;

public interface EventPublisherInterface {
    void publishTaskEvent(TaskEvent event);
}