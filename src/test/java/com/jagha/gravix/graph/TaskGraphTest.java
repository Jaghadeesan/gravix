package com.jagha.gravix.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskGraphTest {

    private TaskGraph graph;

    @BeforeEach
    public void setup() {
        graph = new TaskGraph();
    }

    @Test
    void addNode_NodeAddedSuccessfully() {
        graph.addNode(1L);
        assertTrue(graph.getAllNodes().contains(1L));
    }

    @Test
    void addEdge_BothNodesAddedAndConnectedSuccessfully() {
        graph.addEdge(1L, 2L);
        assertTrue(graph.getAllNodes().contains(1L));
        assertTrue(graph.getAllNodes().contains(2L));
        assertTrue(graph.getDependents(1L).contains(2L));
        assertTrue(graph.getDependencies(2L).contains(1L));
    }

    @Test
    void getDependents_EmptyForLeafNodes() {
        graph.addNode(1L);
        assertTrue(graph.getDependents(1L).isEmpty());
    }
}
