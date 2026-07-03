package com.jagha.collabflow.service;

import com.jagha.collabflow.dto.task.TaskRequest;
import com.jagha.collabflow.dto.task.TaskResponse;
import com.jagha.collabflow.entity.Board;
import com.jagha.collabflow.entity.Task;
import com.jagha.collabflow.entity.TaskStatus;
import com.jagha.collabflow.entity.User;
import com.jagha.collabflow.repository.BoardRespository;
import com.jagha.collabflow.repository.TaskRespository;
import com.jagha.collabflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRespository taskRespository;
    private final BoardRespository boardRespository;
    private final UserRepository userRepository;
    private final AuthHelper authHelper;

    public TaskService(TaskRespository taskRespository, BoardRespository boardRespository, UserRepository userRepository, AuthHelper authHelper) {
        this.taskRespository = taskRespository;
        this.boardRespository = boardRespository;
        this.userRepository = userRepository;
        this.authHelper = authHelper;
    }

    public TaskResponse createTask(TaskRequest request) {

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
        return toResponse(taskRespository.save(task));
    }

    public List<TaskResponse> getTasksByBoard(Long boardId) {
        return taskRespository.findByBoardId(boardId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRespository.findById(id)
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
        return toResponse(taskRespository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = taskRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRespository.delete(task);
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
}
