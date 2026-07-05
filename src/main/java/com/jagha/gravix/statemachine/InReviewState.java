package com.jagha.gravix.statemachine;

import com.jagha.gravix.entity.TaskStatus;

public class InReviewState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_REVIEW;
    }

    @Override
    public TaskState transitionToInProgress() {
        return new InProgressState();
    }

    @Override
    public TaskState transitionToInReview() {
        throw new IllegalStateException(
                "Task already in IN_REVIEW state"
        );
    }

    @Override
    public TaskState transitionToDone() {
        return new DoneState();
    }

    @Override
    public TaskState transitionToToDo() {
        throw new IllegalStateException(
                "Cannot move from IN_REVIEW state back to TODO. Move it to IN_PROGRESS first"
        );
    }
}
