package com.jagha.gravix.dto.sla;

import com.jagha.gravix.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlaTaskItem implements Comparable<SlaTaskItem> {

    private Long taskId;
    private String taskTitle;
    private Long boardId;
    private Long assigneeId;
    private Instant dueDate;
    private TaskStatus status;
    private SlaStatus slaStatus;

    public enum SlaStatus {
        ON_TRACK,
        WARNING,
        BREACHED
    }

    // Min-heap ordering — tasks with earliest due dates
    // have highest priority (come out of heap first)
    @Override
    public int compareTo(SlaTaskItem other) {
        if (this.dueDate == null) return 1;
        if (other.dueDate == null) return -1;
        return this.dueDate.compareTo(other.dueDate);
    }
}
