package com.jagha.gravix.statemachine;

import com.jagha.gravix.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStateMachineTest {

    // Valid Transition tests
    @Test
    void todo_CanTransitionTo_InProgress() {
        TaskState state = new TodoState();
        TaskState next = state.transitionToInProgress();
        assertEquals(TaskStatus.IN_PROGRESS, next.getStatus());
    }

    @Test
    void inProgress_CanTransitionTo_InReview() {
        TaskState state = new InProgressState();
        TaskState next = state.transitionToInReview();
        assertEquals(TaskStatus.IN_REVIEW, next.getStatus());
    }

    @Test
    void inProgress_CanTransitionTo_Done_DirectlyForSimpleTask() {
        TaskState state = new InProgressState();
        TaskState next = state.transitionToDone();
        assertEquals(TaskStatus.DONE, next.getStatus());
    }

    @Test
    void inReview_CanTransitionTo_Done() {
        TaskState state = new InReviewState();
        TaskState next = state.transitionToDone();
        assertEquals(TaskStatus.DONE, next.getStatus());
    }

    @Test
    void inReview_CanTransitionBackTo_InProgress_WhenReviewFails() {
        TaskState state = new InReviewState();
        TaskState next = state.transitionToInProgress();
        assertEquals(TaskStatus.IN_PROGRESS, next.getStatus());
    }

    @Test
    void done_CanReopenTo_Todo() {
        TaskState state = new DoneState();
        TaskState next = state.transitionToToDo();
    }

    // Invalid Transition Tests
    @Test
    void todo_CannotDirectlyTransitionTo_Done() {
        TaskState state = new TodoState();
        IllegalStateException ex = assertThrows(IllegalStateException.class, state::transitionToDone);
        assertTrue(ex.getMessage().contains("TODO"));
    }

    @Test
    void done_CannotTransitionTo_InProgress_Directly() {
        TaskState state = new DoneState();
        assertThrows(IllegalStateException.class, state::transitionToInProgress);
    }

    void done_CannotTransitionTo_InReview() {
        TaskState state = new DoneState();
        assertThrows(IllegalStateException.class, state::transitionToInReview);
    }

    // Factory tests
    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    void factory_CreatesCorrectState_ForEachStatus(TaskStatus status) {
        TaskState state = TaskStateFactory.fromStatus(status);
        assertEquals(status, state.getStatus());
    }

    // Full Workflow tests
    @Test
    void fullWorkFlow_Todo_InProgress_InReview_Done() {
        TaskState state = TaskStateFactory.fromStatus(TaskStatus.TODO);
        state = state.transitionToInProgress();
        assertEquals(TaskStatus.IN_PROGRESS, state.getStatus());
        state = state.transitionToInReview();
        assertEquals(TaskStatus.IN_REVIEW, state.getStatus());
        state = state.transitionToDone();
        assertEquals(TaskStatus.DONE, state.getStatus());
    }

}
