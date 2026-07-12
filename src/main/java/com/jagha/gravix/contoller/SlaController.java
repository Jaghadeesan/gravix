package com.jagha.gravix.contoller;

import com.jagha.gravix.config.SlaProperties;
import com.jagha.gravix.dto.sla.SlaTaskItem;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
@Tag(name = "SLA", description = "SLA monitoring endpoints")
@SecurityRequirement(name = "bearerAuth")
public class SlaController {
    private final TaskRepository taskRepository;
    private final SlaProperties slaProperties;

    @GetMapping("/status")
    @Operation(summary = "Get current SLA status for all active tasks")
    public ResponseEntity<List<SlaTaskItem>> getSlaStatus() {
        List<Task> activeTasks = taskRepository.findActivetasksWithDueDate();

        PriorityQueue<SlaTaskItem> heap = new  PriorityQueue<>(activeTasks.size());
        for (Task task : activeTasks) {
            Instant now = Instant.now();
            SlaTaskItem.SlaStatus status;

            if(task.getDueDate() == null) {
                status = SlaTaskItem.SlaStatus.ON_TRACK;
            } else if (now.isAfter(task.getDueDate())) {
                status = SlaTaskItem.SlaStatus.BREACHED;
            } else {
                long hours = ChronoUnit.HOURS.between(now, task.getDueDate());
                status = hours <=
                        slaProperties.getCriticalThresholdHours()
                        ? SlaTaskItem.SlaStatus.WARNING
                        :  SlaTaskItem.SlaStatus.ON_TRACK;
            }
            heap.offer(SlaTaskItem.builder()
                    .taskId(task.getId())
                    .taskTitle(task.getTitle())
                    .boardId(task.getBoard().getId())
                    .dueDate(task.getDueDate())
                    .status(task.getStatus())
                    .slaStatus(status)
                    .build());
        }

        // Return in priority order (most urgent first)
        List<SlaTaskItem> result = heap.stream()
                .sorted()
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/breached")
    @Operation(summary = "Get only breached tasks")
    public ResponseEntity<List<SlaTaskItem>> getBreachedTasks() {
        return ResponseEntity.ok(
                taskRepository.findActivetasksWithDueDate()
                        .stream()
                        .filter(t -> t.getDueDate() != null
                            && Instant.now().isAfter(t.getDueDate()))
                        .map(t -> SlaTaskItem.builder()
                                .taskId(t.getId())
                                .taskTitle(t.getTitle())
                                .boardId(t.getBoard().getId())
                                .dueDate(t.getDueDate())
                                .status(t.getStatus())
                                .slaStatus(SlaTaskItem.SlaStatus.BREACHED)
                                .build())
                        .sorted()
                        .collect(Collectors.toList()));
    }
}
