package com.jagha.gravix.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CriticalPathCalculatorTest {

    private CriticalPathCalculator calculator;
    private TopologicalSorter sorter;
    private TaskGraph graph;

    @BeforeEach
    public void setUp() {
        calculator = new CriticalPathCalculator();
        sorter = new TopologicalSorter();
        graph = new TaskGraph();
    }

    @Test
    void calculate_SimpleChain_CorrectPath() {
        // 1 → 2 → 3
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 3L);

        CriticalPathResult result =
                calculator.calculate(graph, sorter);

        assertFalse(result.isHasCycle());
        assertTrue(result.isHasPath());
        assertEquals(2, result.getPathLength());
        assertTrue(result.getCriticalPath().contains(1L));
        assertTrue(result.getCriticalPath().contains(3L));
    }

    @Test
    void calculate_ParallelPaths_ChoosesLongest() {
        // Short path: 1 → 3
        // Long path: 1 → 2 → 3
        graph.addEdge(1L, 3L);
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 3L);

        CriticalPathResult result =
                calculator.calculate(graph, sorter);

        assertFalse(result.isHasCycle());
        // Critical path should go through 2
        assertTrue(result.getCriticalPath().contains(2L));
    }

    @Test
    void calculate_WithCycle_ReturnsCycleResult() {
        graph.addEdge(1L, 2L);
        graph.addEdge(2L, 1L);

        CriticalPathResult result =
                calculator.calculate(graph, sorter);

        assertTrue(result.isHasCycle());
        assertFalse(result.isHasPath());
    }

    @Test
    void calculate_SingleNode_NoPath() {
        graph.addNode(1L);

        CriticalPathResult result =
                calculator.calculate(graph, sorter);

        assertFalse(result.isHasCycle());
        assertFalse(result.isHasPath());
    }
}
