package com.jagha.gravix.statemachine;

import com.jagha.gravix.entity.TaskStatus;

public class DoneState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.DONE;
    }

    @Override
    public TaskState transitionToInProgress() {
        throw new IllegalStateException(
                "Cannot reopen a DONE task to IN_PROGRESS directly. Reopen to TODO first."
        );
    }

    @Override
    public TaskState transitionToInReview() {
        throw new IllegalStateException("Cannot move a DONE task to IN_REVIEW.");
    }

    @Override
    public TaskState transitionToDone() {
        throw new IllegalStateException("Task is already DONE.");
    }

    @Override
    public TaskState transitionToToDo() {
        // Reopen completed task
        return new TodoState();
    }
}
