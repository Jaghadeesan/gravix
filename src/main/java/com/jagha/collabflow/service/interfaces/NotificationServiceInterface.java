package com.jagha.collabflow.service.interfaces;

import com.jagha.collabflow.dto.event.TaskEvent;

public interface NotificationServiceInterface {

    void sendTaskAssignmentNotification(TaskEvent event);

    void sendSlaBreachNotification(TaskEvent event);

    void sendStatusChangeNotification(TaskEvent event);

}
