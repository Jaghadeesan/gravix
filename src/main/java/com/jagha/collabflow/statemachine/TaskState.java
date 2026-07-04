package com.jagha.collabflow.statemachine;


import com.jagha.collabflow.entity.TaskStatus;

public interface TaskState {

    // Returns the status this state represents
    TaskStatus getStatus();

    // Transition to IN_PROGRESS - throws if invalid from this State
    TaskState transitionToInProgress();

    // Transition to IN_REVIEW
    TaskState transitionToInReview();

    // Transition to DONE
    TaskState transitionToDone();

    // Transistion back to TO DO (for reopening tasks)
    TaskState transitionToToDo();

}
