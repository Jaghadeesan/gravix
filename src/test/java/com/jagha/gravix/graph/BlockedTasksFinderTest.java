package com.jagha.gravix.graph;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockedTasksFinderTest {

    private BlockedTasksFinder finder;
    private TaskGraph graph;

    @BeforeEach
    public void setUp() {
        finder = new BlockedTasksFinder();
        graph = new TaskGraph();
    }

    @Test
    void findAllBlockedTasks_DirectAndTransitive() {
        // 1 blocks 2, 2 blocks 3
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 3L);

        Set<Long> blocked =
                finder.findAllBlockedTasks(graph, 1L);

        assertEquals(2, blocked.size());
        assertTrue(blocked.contains(2L));
        assertTrue(blocked.contains(3L));
    }

    @Test
    void findAllBlockedTasks_NoBlockedTasks() {
        graph.addNode(1L);

        Set<Long> blocked =
                finder.findAllBlockedTasks(graph, 1L);

        assertTrue(blocked.isEmpty());
    }

    @Test
    void findAllBlockedTasks_DiamondShape() {
        // 1 blocks 2 and 3, both block 4
        graph.addEdge(1L, 2L);
        graph.addEdge(1L, 3L);
        graph.addEdge(2L, 4L);
        graph.addEdge(3L, 4L);

        Set<Long> blocked =
                finder.findAllBlockedTasks(graph, 1L);

        assertEquals(3, blocked.size());
        assertTrue(blocked.contains(2L));
        assertTrue(blocked.contains(3L));
        assertTrue(blocked.contains(4L));
    }

    @Test
    void findDirectBlockers_ReturnsImmediatePredecessors() {
        graph.addEdge(1L, 3L);
        graph.addEdge(2L, 3L);

        // Task 3 is directly blocked by 1 and 2
        var blockers = finder.findDirectBlockers(graph, 3L);

        assertEquals(2, blockers.size());
        assertTrue(blockers.contains(1L));
        assertTrue(blockers.contains(2L));
    }
}
