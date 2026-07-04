package com.jagha.collabflow.statemachine;

import com.jagha.collabflow.entity.TaskStatus;

public class DoneState implements TaskState {
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.DONE;
    }

    @Override
    public TaskState transistionToInProgress() {
        throw new IllegalStateException(
                "Cannot reopen a DONE task to IN_PROGRESS directly. Reopen to TODO first."
        );
    }

    @Override
    public TaskState transistionToInReview() {
        throw new IllegalStateException("Cannot move a DONE task to IN_REVIEW.");
    }

    @Override
    public TaskState transistionToDone() {
        throw new IllegalStateException("Task is already DONE.");
    }

    @Override
    public TaskState transitionToToDo() {
        // Reopen completed task
        return new TodoState();
    }
}
