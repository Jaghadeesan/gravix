package com.jagha.collabflow.statemachine;

import com.jagha.collabflow.entity.TaskStatus;

public class InReviewState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_REVIEW;
    }

    @Override
    public TaskState transistionToInProgress() {
        return new InProgressState();
    }

    @Override
    public TaskState transistionToInReview() {
        throw new IllegalStateException(
                "Task already in IN_REVIEW state"
        );
    }

    @Override
    public TaskState transistionToDone() {
        return new DoneState();
    }

    @Override
    public TaskState transitionToToDo() {
        throw new IllegalStateException(
                "Cannot move from IN_REVIEW state back to TODO. Move it to IN_PROGRESS first"
        );
    }
}
