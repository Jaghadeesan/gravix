package com.jagha.collabflow.statemachine;

import com.jagha.collabflow.entity.TaskStatus;

public class TodoState implements TaskState{
    @Override
    public TaskStatus getStatus() {
        return TaskStatus.TODO;
    }

    @Override
    public TaskState transistionToInProgress() {
        return new InProgressState();
    }

    @Override
    public TaskState transistionToInReview() {
        throw new IllegalStateException(
                "Cannot move to IN_REVIEW directly from TODO. Move to IN_PROGRESS first");
    }

    @Override
    public TaskState transistionToDone() {
        throw new IllegalStateException(
                "Cannot move to DONE directly from TODO. Complete the workflow first");
    }

    @Override
    public TaskState transitionToToDo() {
        throw new IllegalStateException(
                "Task already in TODO state");
    }
}
