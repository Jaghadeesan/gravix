package com.jagha.gravix.contoller;

import com.jagha.gravix.dto.task.TaskRequest;
import com.jagha.gravix.dto.task.TaskResponse;
import com.jagha.gravix.entity.TaskStatus;
import com.jagha.gravix.service.TaskService;
import com.jagha.gravix.service.interfaces.TaskServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskServiceInterface taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "Get all tasks for a Board")
    public ResponseEntity<List<TaskResponse>> getTasksByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(taskService.getTasksByBoard(boardId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Task by Id")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task by id")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task by id")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition task to new status",
                description = "Validates transition via State Machine. Invalid transistions return 400")
    public ResponseEntity<TaskResponse> transitionStatus(@PathVariable Long id, @RequestParam TaskStatus newStatus) {
        return ResponseEntity.ok(taskService.transitionTaskStatus(id, newStatus));
    }
}
