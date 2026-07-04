package com.jagha.collabflow.statemachine;

import com.jagha.collabflow.entity.TaskStatus;

public class TaskStateFactory {

    public static TaskState fromStatus(TaskStatus status) {
        return switch (status) {
            case TODO -> new TodoState();
            case IN_PROGRESS -> new InProgressState();
            case IN_REVIEW -> new InReviewState();
            case DONE -> new DoneState();
        };
    }
}
