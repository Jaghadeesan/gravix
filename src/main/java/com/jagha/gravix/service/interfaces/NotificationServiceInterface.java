package com.jagha.gravix.service.interfaces;

import com.jagha.gravix.dto.event.TaskEvent;

public interface NotificationServiceInterface {

    void sendTaskAssignmentNotification(TaskEvent event);

    void sendSlaBreachNotification(TaskEvent event);

    void sendStatusChangeNotification(TaskEvent event);

}
