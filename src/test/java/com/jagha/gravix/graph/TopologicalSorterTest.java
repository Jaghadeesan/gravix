package com.jagha.gravix.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSorterTest {

    private TopologicalSorter sorter;
    private TaskGraph graph;

    @BeforeEach
    void setUp() {
        sorter = new TopologicalSorter();
        graph = new TaskGraph();
    }

    @Test
    void sort_SimpleChain_CorrectOrder() {
        // 1 -> 2 -> 3
        graph.addEdge(1L,2L);
        graph.addEdge(2L,3L);

        TopologicalSortResult result = sorter.sort(graph);

        assertFalse(result.isHasCycle());
        List<Long> order = result.getSortedOrder();
        assertTrue(order.indexOf(1L) < order.indexOf(2L));
        assertTrue(order.indexOf(2L) < order.indexOf(3L));
    }

    @Test
    void sort_ParallelSort_AllIncluded() {
        // 1 -> 3, 2 -> 3 (parallel tasks converging)
        graph.addEdge(1L,3L);
        graph.addEdge(2L,3L);

        TopologicalSortResult result = sorter.sort(graph);

        assertFalse(result.isHasCycle());
        assertEquals(3, result.getSortedOrder().size());
        assertTrue(result.getSortedOrder().indexOf(3L) > result.getSortedOrder().indexOf(1L));
        assertTrue(result.getSortedOrder().indexOf(3L) > result.getSortedOrder().indexOf(2L));
    }

    @Test
    void sort_SingleNode_ReturnsNode() {
        graph.addNode(1L);
        TopologicalSortResult result = sorter.sort(graph);
        assertFalse(result.isHasCycle());
        assertEquals(1, result.getSortedOrder().size());
    }

    @Test
    void sort_EmptyGraph_ReturnsEmpty() {
        TopologicalSortResult result = sorter.sort(graph);
        assertFalse(result.isHasCycle());
        assertTrue(result.getSortedOrder().isEmpty());
    }
}
