package com.jagha.collabflow.statemachine;


import com.jagha.collabflow.entity.TaskStatus;

public interface TaskState {

    // Returns the status this state represents
    TaskStatus getStatus();

    // Transistion to IN_PROGRESS - throws if invalid from this State
    TaskState transistionToInProgress();

    // Transistion to IN_REVIEW
    TaskState transistionToInReview();

    // Transistion to DONE
    TaskState transistionToDone();

    // Transistion back to TO DO (for reopening tasks)
    TaskState transitionToToDo();

}
