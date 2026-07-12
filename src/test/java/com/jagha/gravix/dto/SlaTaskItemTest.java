package com.jagha.gravix.dto.sla;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class SlaTaskItemTest {

    @Test
    void compareTo_EarlierDueDateHasHigherPriority() {
        SlaTaskItem earlier = SlaTaskItem.builder()
                .taskId(1L)
                .dueDate(Instant.now().minus(2, ChronoUnit.HOURS))
                .build();

        SlaTaskItem later = SlaTaskItem.builder()
                .taskId(2L)
                .dueDate(Instant.now().plus(2, ChronoUnit.HOURS))
                .build();

        assertTrue(earlier.compareTo(later) < 0);
    }

    @Test
    void priorityQueue_PollsEarliestDueDateFirst() {
        PriorityQueue<SlaTaskItem> heap = new PriorityQueue<>();

        SlaTaskItem task1 = SlaTaskItem.builder()
                .taskId(1L)
                .taskTitle("Later task")
                .dueDate(Instant.now().plus(5, ChronoUnit.HOURS))
                .build();

        SlaTaskItem task2 = SlaTaskItem.builder()
                .taskId(2L)
                .taskTitle("Earlier task")
                .dueDate(Instant.now().minus(1, ChronoUnit.HOURS))
                .build();

        SlaTaskItem task3 = SlaTaskItem.builder()
                .taskId(3L)
                .taskTitle("Middle task")
                .dueDate(Instant.now().plus(2, ChronoUnit.HOURS))
                .build();

        heap.offer(task1);
        heap.offer(task2);
        heap.offer(task3);

        // Min-heap — earliest due date polls first
        assertEquals(2L, heap.poll().getTaskId());
        assertEquals(3L, heap.poll().getTaskId());
        assertEquals(1L, heap.poll().getTaskId());
    }

    @Test
    void compareTo_NullDueDateGoesLast() {
        SlaTaskItem withDate = SlaTaskItem.builder()
                .taskId(1L)
                .dueDate(Instant.now())
                .build();

        SlaTaskItem withoutDate = SlaTaskItem.builder()
                .taskId(2L)
                .dueDate(null)
                .build();

        assertTrue(withDate.compareTo(withoutDate) < 0);
    }
}