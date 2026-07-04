package com.jagha.collabflow.statemachine;

import com.jagha.collabflow.entity.TaskStatus;

public class InProgressState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public TaskState transistionToInProgress() {
        throw new IllegalStateException(
                "Task is already IN_PROGRESS");
    }

    @Override
    public TaskState transistionToInReview() {
        return new InReviewState();
    }

    @Override
    public TaskState transistionToDone() {
        // Allow direct done from in-progress for simple tasks
        return new DoneState();
    }

    @Override
    public TaskState transitionToToDo() {
        // Allow moving back — task was reopened
        return new TodoState();
    }
}
