package com.jagha.gravix.contoller;

import com.jagha.gravix.dto.dependency.AddDependencyRequest;
import com.jagha.gravix.dto.dependency.BlockedTasksResponse;
import com.jagha.gravix.dto.dependency.DependencyGraphResponse;
import com.jagha.gravix.service.interfaces.DependencyServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dependencies")
@RequiredArgsConstructor
@Tag(name = "Dependencies",
        description = "Task dependency graph management")
@SecurityRequirement(name = "bearerAuth")
public class DependencyController {

    private final DependencyServiceInterface dependencyService;

    @PostMapping
    @Operation(
            summary = "Add task dependency",
            description = "Add a dependency edge"
    )
    public ResponseEntity<DependencyGraphResponse> addDependency(
            @Valid @RequestBody AddDependencyRequest request) {
        return ResponseEntity.ok(dependencyService.addDependency(request));
    }

    @DeleteMapping
    @Operation(summary = "Remove Task dependency")
    public ResponseEntity<DependencyGraphResponse> removeDependency(
            @RequestParam Long dependencyTaskId,
            @RequestParam Long dependentTaskId) {
        dependencyService.removeDependency(dependencyTaskId, dependentTaskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/board/{boardId}/graph")
    @Operation(
            summary = "Get full dependency graph for a board",
            description = "Returns topological order, " +
                    "critical path, and cycle detection results"
    )
    public ResponseEntity<DependencyGraphResponse> getBoardGraph(@PathVariable Long boardId) {
        return ResponseEntity.ok(dependencyService.getBoardDependencyGraph(boardId));
    }

    @GetMapping("/tasks/{taskId}/blocked")
    @Operation(
            summary = "Get all tasks blocked by this task",
            description = "BFS traversal to find all direct and transitive dependents"
    )
    public ResponseEntity<BlockedTasksResponse> getBlockedTasks(@PathVariable Long taskId) {
        return ResponseEntity.ok(dependencyService.getBlockedTasks(taskId));
    }
}
