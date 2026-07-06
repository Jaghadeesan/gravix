package com.jagha.gravix.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CriticalPathCalculator {

    /**
     * Critical Path Method (CPM) using dynamic programming
     * on the topologically sorted DAG.
     *
     * Algorithm:
     * 1. Topologically sort the graph
     * 2. For each node in topological order, calculate
     *    the longest path from any source to that node
     * 3. The critical path is the path with maximum length
     *
     * Time complexity: O(V + E)
     *
     * Real-world meaning: if any task on the critical path
     * is delayed, the entire project is delayed by the same amount.
     */
    public CriticalPathResult calculate(TaskGraph graph, TopologicalSorter sorter) {
        TopologicalSortResult sorterResult = sorter.sort(graph);

        if(sorterResult.isHasCycle()) {
            log.warn("[CRITICAL_PATH] Cannot calculate " +
                    "critical path - graph has a cycle");
            return CriticalPathResult.withCycle();
        }

        List<Long> topoOrder = sorterResult.getSortedOrder();
        Map<Long, List<Long>> adjacencyList = graph.getAdjacencyList();

        // dp[node] = longest path length ending at this node
        Map<Long, Integer> dp = new HashMap<>();
        // predecessor[node] = previous node on longest path
        Map<Long, Long> predecessor = new HashMap<>();

        for(Long node : topoOrder) {
            dp.put(node, 0);
        }

        // Process in topological order
        for(Long node : topoOrder) {
            for(Long neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                int newDistance = dp.get(node) + 1;
                if(newDistance > dp.getOrDefault(neighbor, 0)) {
                    dp.put(neighbor, newDistance);
                    predecessor.put(neighbor, node);
                }
            }
        }

        // Find the node with maximum distance
        Long endNode = topoOrder.stream()
                .max(Comparator.comparingInt(n -> dp.getOrDefault(n, 0)))
                .orElse(null);

        if (endNode == null || dp.get(endNode) == 0) {
            return CriticalPathResult.noPath();
        }

        // Reconstruct the critical path
        List<Long> criticalPath = new  ArrayList<>();
        Long current = endNode;
        while(current != null) {
            criticalPath.add(0, current);
            current = predecessor.get(current);
        }

        log.info("[CRITICAL_PATH] Critical path calculated. " +
                        "Length={}, Path={}",
                dp.get(endNode), criticalPath);

        return CriticalPathResult.success(criticalPath, dp.get(endNode));

    }
}
