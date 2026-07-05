package com.jagha.gravix.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TaskGraph {

    // Adjacency list representation
    // Key: taskId, Value: list of taskIds it depends on
    private final Map<Long, List<Long>> adjacencyList;

    // Reverse Adjacency List
    // Key: taskId, Value: List of taskIds it depend on
     private final Map<Long, List<Long>> reverseAdjacencyList;

     // All taskIds in the graph
     private final Set<Long> allNodes;

    public TaskGraph() {
        this.adjacencyList = new HashMap<>();
        this.reverseAdjacencyList = new HashMap<>();
        this.allNodes = new HashSet<>();
    }

    // Add a node(task) to the graph
    public void addNode(Long taskId) {
        allNodes.add(taskId);
        adjacencyList.putIfAbsent(taskId, new ArrayList<>());
        reverseAdjacencyList.putIfAbsent(taskId, new ArrayList<>());
    }

    // Add directed edge: dependency edge: dependency -> dependentTask
    // Meaning: dependent task cannot start until dependency is done
    public void addEdge(Long dependencyTaskId, Long dependentTaskId) {
        addNode(dependencyTaskId);
        addNode(dependentTaskId);
        adjacencyList.get(dependencyTaskId).add(dependentTaskId);
        reverseAdjacencyList.get(dependentTaskId).add(dependencyTaskId);
    }

    public Map<Long, List<Long>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    public Map<Long, List<Long>> getReverseAdjacencyList() {
        return Collections.unmodifiableMap(reverseAdjacencyList);
    }

    public Set<Long> getAllNodes() {
        return Collections.unmodifiableSet(allNodes);
    }

    public List<Long> getDependents(Long taskId) {
        return adjacencyList.getOrDefault(taskId, Collections.emptyList());
    }

    public List<Long> getDependencies(Long taskId) {
        return reverseAdjacencyList.getOrDefault(taskId, Collections.emptyList());
    }
}
