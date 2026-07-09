package com.jagha.gravix.service;

import com.jagha.gravix.dto.dependency.AddDependencyRequest;
import com.jagha.gravix.dto.dependency.BlockedTasksResponse;
import com.jagha.gravix.dto.dependency.DependencyGraphResponse;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.entity.TaskDependency;
import com.jagha.gravix.graph.*;
import com.jagha.gravix.repository.TaskDependencyRepository;
import com.jagha.gravix.repository.TaskRepository;
import com.jagha.gravix.service.interfaces.DependencyServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DependencyService implements DependencyServiceInterface {

    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskRepository taskRepository;
    private final TopologicalSorter topologicalSorter;
    private final CycleDetector cycleDetector;
    private final CriticalPathCalculator criticalPathCalculator;
    private final BlockedTasksFinder blockedTasksFinder;

    @Override
    @Transactional
    public DependencyGraphResponse addDependency(AddDependencyRequest request) {
        log.info("[DEPENDENCY] Adding dependency. dependencyTask={}, dependentTask={}",
                request.getDependencyTaskId(), request.getDependentTaskId());

        // Self-dependency check
        if(request.getDependencyTaskId().equals(request.getDependentTaskId())) {
            throw new IllegalArgumentException("A task cannot depend on itself");
        }

        // Duplicate check
        if(taskDependencyRepository.existsByDependentTaskIdAndDependencyTaskId(request.getDependentTaskId(), request.getDependencyTaskId())) {
            throw new IllegalArgumentException("This dependency already exists");
        }

        // Load current graph for the board
        Task dependencyTask = taskRepository.findById(request.getDependentTaskId())
                .orElseThrow(() -> new RuntimeException("Dependency task not found"));

        Long boardId = dependencyTask.getBoard().getId();
        TaskGraph currentGraph = buildGraphForBoard(boardId);

        // Cycle detection BEFORE saving
        CycleDetectionResult cycleResult = cycleDetector.detectCycle(currentGraph, request.getDependencyTaskId(), request.getDependentTaskId());

        if(cycleResult.isHasCycle()) {
            log.warn("[DEPENDENCY] Cycle detected. Path: {}", cycleResult.getCyclePath());
            throw new IllegalArgumentException("Adding this dependency would create a circular dependency. Cycle path: "
                    + cycleResult.getCyclePath());
        }

        // Safe to save
        Task dependentTask = taskRepository.findById(request.getDependentTaskId())
                .orElseThrow(() -> new RuntimeException("Dependent task not found"));

        TaskDependency dependency = new TaskDependency();
        dependency.setDependencyTask(dependencyTask);
        dependency.setDependentTask(dependentTask);
        taskDependencyRepository.save(dependency);

        log.info("[DEPENDENCY] Dependency added successfully. " +
                "dependencyTaskId={}, dependentTaskId={}",
                request.getDependencyTaskId(), request.getDependentTaskId());
        return getBoardDependencyGraph(boardId);
    }

    @Override
    @Transactional
    public void removeDependency(Long dependencyTaskId, Long dependentTaskId) {
        TaskDependency dependency = taskDependencyRepository.findByDependentTaskIdAndDependencyTaskId(dependentTaskId, dependencyTaskId)
                .orElseThrow(() -> new RuntimeException("Dependency not found"));
        taskDependencyRepository.delete(dependency);
        log.info("[DEPENDENCY] Dependency removed. " +
                "dependentTaskId={}, dependencyTaskId={}"
                , dependentTaskId, dependencyTaskId);
    }

    @Override
    @Transactional(readOnly = true)
    public DependencyGraphResponse getBoardDependencyGraph(Long boardId) {
        TaskGraph graph = buildGraphForBoard(boardId);
        TopologicalSortResult topoResult =  topologicalSorter.sort(graph);

        CriticalPathResult criticalPathResult = criticalPathCalculator.calculate(graph, topologicalSorter);
        return DependencyGraphResponse.builder()
                .boardId(boardId)
                .topologicalOrder(topoResult.getSortedOrder())
                .criticalPath(criticalPathResult.getCriticalPath())
                .criticalPathLength(
                        criticalPathResult.getPathLength())
                .hasCycle(topoResult.isHasCycle())
                .cyclePath(List.of())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BlockedTasksResponse getBlockedTasks(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        TaskGraph graph = buildGraphForBoard(task.getBoard().getId());

        Set<Long> allBlocked = blockedTasksFinder.findAllBlockedTasks(graph, taskId);

        List<Long> directBlockers = blockedTasksFinder.findDirectBlockers(graph, taskId);

        return BlockedTasksResponse.builder()
                .taskId(taskId)
                .allBlockedTaskIds(allBlocked)
                .directBlockerIds(directBlockers)
                .totalBlockedCount(allBlocked.size())
                .build();
    }

    private TaskGraph buildGraphForBoard(Long boardId) {
        TaskGraph graph = new TaskGraph();

        // Add all tasks as nodes
        taskRepository.findByBoardId(boardId).forEach(task -> graph.addNode(task.getId()));

        // Add all dependency edges
        taskDependencyRepository.findByBoardId(boardId).forEach(dep ->
                graph.addEdge(dep.getDependencyTask().getId(), dep.getDependentTask().getId()));
        return graph;
    }
}
