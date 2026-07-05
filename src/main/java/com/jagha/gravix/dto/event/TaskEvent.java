package com.jagha.gravix.dto.event;

import com.jagha.gravix.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEvent {

    private String eventId;
    private String eventType;
    private Long taskId;
    private String taskTitle;
    private Long boardId;
    private Long assigneeId;
    private TaskStatus previousStatus;
    private TaskStatus newStatus;
    private Long triggeredByUserId;
    private Instant occurredAt;

    // Event type constants
    public static final String TASK_CREATED = "TASK_CREATED";
    public static final String TASK_STATUS_CHANGED = "TASK_STATUS_CHANGED";
    public static final String TASK_ASSIGNED = "TASK_ASSIGNED";
    public static final String TASK_DELETED = "TASK_DELETED";
    public static final String SLA_BREACHED = "SLA_BREACHED";
}
