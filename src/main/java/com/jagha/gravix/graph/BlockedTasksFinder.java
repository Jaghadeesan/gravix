package com.jagha.gravix.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BlockedTasksFinder {
    /**
     * BFS traversal to find ALL tasks blocked
     * by a given task (directly and transitively).
     *
     * Example: if Task A blocks Task B which blocks Task C,
     * this returns both B and C when called with Task A.
     *
     * Time complexity: O(V + E)
     */
    public Set<Long> findAllBlockedTasks(TaskGraph graph, Long taskId) {
        Set<Long> blocked = new LinkedHashSet<>();
        Queue<Long> queue = new LinkedList<>();

        // Start with direct dependencies
        List<Long> directDependents = graph.getDependents(taskId);
        queue.addAll(directDependents);
        while(!queue.isEmpty()) {
            Long current = queue.poll();

            if(blocked.contains(current)) {
                continue; // already visited
            }

            blocked.add(current);

            // Add this task's dependents to queue
            List<Long> nextDependents = graph.getDependents(current);
            queue.addAll(nextDependents);
        }
        log.info("[BFS_BLOCKED] Task {} blocks {} tasks: {}", taskId, blocked.size(), blocked);

        return blocked;
    }

    /**
     * Find tasks that directly block a given task
     * (immediate predecessors only, not transitive)
     */
    public List<Long> findDirectBlockers(
            TaskGraph graph,
            Long taskId) {
        return graph.getDependencies(taskId);
    }
}
