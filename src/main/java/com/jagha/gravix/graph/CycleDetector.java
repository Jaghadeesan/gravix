package com.jagha.gravix.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CycleDetector {
    /**
     * DFS-based cycle detection.
     * Returns the exact cycle path so users know
     * which tasks form the circular dependency.
     *
     * Uses three-color marking:
     * WHITE (0) = unvisited
     * GRAY  (1) = currently being processed (in DFS stack)
     * BLACK (2) = fully processed
     *
     * A cycle exists when DFS encounters a GRAY node
     * (back edge to an ancestor in current path)
     *
     * Time complexity: O(V + E)
     */
    public CycleDetectionResult detectCycle(TaskGraph graph, Long newDependencyId, Long newDependentId) {
        // Temporarily add the new edge to check
        // if it would create a cycle
        TaskGraph testGraph = copyGraph(graph);
        testGraph.addEdge(newDependencyId, newDependentId);

        Map<Long, Integer> color = new HashMap<>();
        Map<Long, Long> parent = new HashMap<>();

        for(Long node : testGraph.getAllNodes()) {
            color.put(node, 0); // WHITE
        }

        List<Long> cyclePath = new ArrayList<>();

        for(Long node :  testGraph.getAllNodes()) {
            if(color.get(node) == 0) {
                if(dfs(node, testGraph, color, parent, cyclePath)) {
                    log.warn("[CYCLE_DETECT] Cycle found: {}", cyclePath);
                    return CycleDetectionResult.cycleFound(cyclePath);
                }
            }
        }
        return  CycleDetectionResult.noCycle();
    }

    private boolean dfs(Long node, TaskGraph graph, Map<Long, Integer> color,
                        Map<Long, Long> parent, List<Long> cyclePath) {
        color.put(node, 1); // GRAY - in progress

        for(Long neighbor : graph.getDependents(node)) {
            if(color.get(neighbor) == 1) {
                // Found cycle - reconstruct path
                reconstructCyclePath(node, neighbor, parent, cyclePath);
                return true;
            }
            if(color.get(neighbor) == 0) {
                parent.put(neighbor, node);
                if(dfs(neighbor, graph, color, parent, cyclePath)) {
                    return true;
                }
            }
        }
        color.put(node, 2); // BLACK - Done
        return false;
    }

    private void reconstructCyclePath(Long cycleStart, Long cycleEnd, Map<Long, Long> parent, List<Long> cyclePath) {
        cyclePath.add(cycleEnd);
        Long current = cycleStart;
        while(current != null && !current.equals(cycleEnd)) {
            cyclePath.add(0, current);
            current = parent.get(current);
        }
        cyclePath.add(0, cycleEnd);
    }

    private TaskGraph copyGraph(TaskGraph original) {
        TaskGraph copy = new TaskGraph();
        for(Long node : original.getAllNodes()) {
            copy.addNode(node);
        }
        for(Map.Entry<Long, List<Long>> entry : original.getAdjacencyList().entrySet()) {
            for(Long neighbor : entry.getValue()) {
                copy.addEdge(entry.getKey(), neighbor);
            }
        }
        return copy;
    }
}
