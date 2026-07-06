package com.jagha.gravix.service;

import com.jagha.gravix.dto.event.TaskEvent;
import com.jagha.gravix.dto.task.TaskRequest;
import com.jagha.gravix.dto.task.TaskResponse;
import com.jagha.gravix.entity.Board;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.entity.TaskStatus;
import com.jagha.gravix.entity.User;
import com.jagha.gravix.repository.BoardRespository;
import com.jagha.gravix.repository.TaskRepository;
import com.jagha.gravix.repository.UserRepository;
import com.jagha.gravix.service.interfaces.EventPublisherInterface;
import com.jagha.gravix.service.interfaces.TaskServiceInterface;
import com.jagha.gravix.statemachine.TaskState;
import com.jagha.gravix.statemachine.TaskStateFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements TaskServiceInterface {

    private final TaskRepository taskRepository;
    private final BoardRespository boardRespository;
    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final EventPublisherInterface eventPublisher;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("[TASK_CREATE] Creating task. boardId={}, title={}",
                request.getBoardId(), request.getTitle());

        Board board = boardRespository.findById(request.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.TODO);
        task.setBoard(board);

        // Assign to user if assignee id is provided
        if(request.getAssigneeId() != null) {
            User user = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(user);
        }

        Task saved = taskRepository.save(task);

        log.info("[TASK_CREATE] Task created successfully. taskId={}, boardId={}",
                saved.getId(), saved.getBoard().getId());

        // Publish created event
        TaskEvent event = TaskEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(TaskEvent.TASK_CREATED)
                .taskId(saved.getId())
                .taskTitle(saved.getTitle())
                .boardId(saved.getBoard().getId())
                .newStatus(TaskStatus.TODO)
                .occurredAt(java.time.Instant.now())
                .build();

        eventPublisher.publishTaskEvent(event);

        return toResponse(saved);
    }

    public List<TaskResponse> getTasksByBoard(Long boardId) {
        return taskRepository.findByBoardId(boardId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        // Status update handled separately via state machine (Day 4)
        // Only update status here if explicitly provided
        if(request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if(request.getAssigneeId() != null) {
            User user = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(user);
        }
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    // Convert entity to DTO - keeps controller/service clean
    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setBoardId(task.getBoard().getId());
        response.setBoardName(task.getBoard().getName());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        // Null check — assignee is optional, task can exist without one
        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeName(task.getAssignee().getFullName());
        }

        return response;
    }

    @Override
    @Transactional
    public TaskResponse transitionTaskStatus(Long taskId, TaskStatus newStatus) {
        log.info("[TASK_TRANSITION] Starting status transition. taskId={}, targetStatus={}",
                taskId, newStatus);
        User currentUser = authHelper.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("[TASK_TRANSITION] Task not found. taskId={}", taskId);
                    return new RuntimeException("Task not found: " + taskId);
                });

        TaskStatus previousStatus = task.getStatus();

        // Guard - no-op if already in target status
        if(previousStatus == newStatus) {
            log.info("[TASK_TRANSITION] Task already in target status. " +
                    "taskId={}, status={}", taskId, newStatus);
            return toResponse(task);
        }

        // Apply state machine transition — throws IllegalStateException
        // if transition is invalid (e.g. TO DO → DONE)
        TaskState currentState = TaskStateFactory.fromStatus(previousStatus);
        TaskState newState = switch(newStatus) {
            case TODO -> currentState.transitionToToDo();
            case IN_PROGRESS -> currentState.transitionToInProgress();
            case IN_REVIEW -> currentState.transitionToInReview();
            case DONE -> currentState.transitionToDone();
        };

        task.setStatus(newState.getStatus());
        Task savedTask = taskRepository.save(task);

        log.info("[TASK_TRANSITION] Status transition successful. " +
                        "taskId={}, from={}, to={}, updatedBy={}",
                taskId, previousStatus, newStatus, currentUser.getEmail());

        // Publish Kafka event — happens after successful DB save
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TaskEvent.TASK_STATUS_CHANGED)
                .taskId(savedTask.getId())
                .taskTitle(savedTask.getTitle())
                .boardId(savedTask.getBoard().getId())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .triggeredByUserId(currentUser.getId())
                .occurredAt(Instant.now())
                .build();

        eventPublisher.publishTaskEvent(event);
        return toResponse(savedTask);
    }
}
