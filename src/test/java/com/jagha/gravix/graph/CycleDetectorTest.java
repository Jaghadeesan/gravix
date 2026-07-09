package com.jagha.gravix.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CycleDetectorTest {

    private CycleDetector detector;
    private TaskGraph graph;

    @BeforeEach
    void setUp() {
        detector = new CycleDetector();
        graph = new TaskGraph();
    }

    @Test
    void detectCycle_NoCycle_ReturnsNoCycle() {
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 3L);

        // Try adding 3 → 4 (no cycle)
        CycleDetectionResult result = detector.detectCycle(graph, 3L, 4L);

        assertFalse(result.isHasCycle());
        assertTrue(result.getCyclePath().isEmpty());
    }

    @Test
    void detectCycle_DirectCycle_DetectsAndReturnsPath() {
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 3L);

        // Try adding 3 → 1 (creates cycle 1→2→3→1)
        CycleDetectionResult result = detector.detectCycle(graph, 3L, 1L);

        assertTrue(result.isHasCycle());
        assertFalse(result.getCyclePath().isEmpty());
    }

    @Test
    void detectCycle_SelfLoop_DetectsCycle() {
        graph.addNode(1L);

        // Task depending on itself
        CycleDetectionResult result =
                detector.detectCycle(graph, 1L, 1L);

        assertTrue(result.isHasCycle());
    }

    @Test
    void detectCycle_ComplexGraph_NoCycle() {
        // Diamond shape: 1→2, 1→3, 2→4, 3→4
        graph.addEdge(1L, 2L);
        graph.addEdge(1L, 3L);
        graph.addEdge(2L, 4L);
        graph.addEdge(3L, 4L);

        // Try adding 4 → 5 (no cycle)
        CycleDetectionResult result =
                detector.detectCycle(graph, 4L, 5L);

        assertFalse(result.isHasCycle());
    }
}
