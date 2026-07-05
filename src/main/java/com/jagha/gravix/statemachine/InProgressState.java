package com.jagha.gravix.statemachine;

import com.jagha.gravix.entity.TaskStatus;

public class InProgressState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public TaskState transitionToInProgress() {
        throw new IllegalStateException(
                "Task is already IN_PROGRESS");
    }

    @Override
    public TaskState transitionToInReview() {
        return new InReviewState();
    }

    @Override
    public TaskState transitionToDone() {
        // Allow direct done from in-progress for simple tasks
        return new DoneState();
    }

    @Override
    public TaskState transitionToToDo() {
        // Allow moving back — task was reopened
        return new TodoState();
    }
}
