package com.jagha.gravix.graph;

import com.jagha.gravix.entity.TaskDependency;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TopologicalSorter {

    /**
     * Kahn's Algorithm for Topological Sort.
     *
     * Algorithm:
     * 1. Calculate in-degree (number of incoming edges) for each node
     * 2. Add all nodes with in-degree 0 to a queue (no dependencies)
     * 3. Process queue: take a node, add to result, reduce in-degree
     *    of its neighbors. If neighbor's in-degree becomes 0, add to queue
     * 4. If result size != total nodes → cycle detected
     *
     * Time complexity: O(V + E) where V = tasks, E = dependencies
     * Space complexity: O(V)
     */

    public TopologicalSortResult sort(TaskGraph graph) {
        Map<Long, List<Long>> adjacencyList = graph.getAdjacencyList();
        Set<Long> allNodes = graph.getAllNodes();

        // Step 1: Calculate in-degree for each node
        Map<Long, Integer> indegrees = new HashMap<>();
        for(Long node : allNodes) {
            indegrees.put(node, 0);
        }

        for(Long node : allNodes) {
            for(Long neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                indegrees.merge(neighbor, 1, Integer::sum);
            }
        }

        // Step 2: Initialize queue with all zero in-degree nodes
        Queue<Long> queue = new LinkedList<>();
        for(Long node : allNodes) {
            if(indegrees.get(node) == 0) {
                queue.offer(node);
            }
        }

        // Step 3: Process queue
        List<Long> sortedOrder = new ArrayList<>();
        while(!queue.isEmpty()) {
            Long current = queue.poll();
            sortedOrder.add(current);

            for(Long neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
                indegrees.merge(neighbor, -1, Integer::sum);
                if(indegrees.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // Step 4: Check for cycle
        if(sortedOrder.size() != allNodes.size()) {
            log.warn("[TPOP SORT] Cycle detected. " +
                    "Processed {}/{} nodes. ",
                    sortedOrder.size(), allNodes.size());
            return TopologicalSortResult.withCycle();
        }

        log.info("[TOPO SORT] Topological Sort Completed. " +
                "Order: {}", sortedOrder);
        return TopologicalSortResult.success(sortedOrder);
    }
}
