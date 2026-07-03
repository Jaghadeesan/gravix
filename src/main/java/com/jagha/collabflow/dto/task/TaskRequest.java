package com.jagha.collabflow.dto.task;

import com.jagha.collabflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class TaskRequest {

    @NotBlank(message = "Task tile is required")
    @Size(min = 2, max = 200, message = "Task title should be between 2 to 200 characters")
    private String title;

    @Size(max = 2000, message = "Task description should not exceed 2000 characters")
    private String description;

    @NotNull(message = "Board ID is required")
    private Long boardId;

    private Long assigneeId;

    private Instant DueDate;

    private TaskStatus status;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, Long boardId, Long assigneeId, Instant dueDate, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.boardId = boardId;
        this.assigneeId = assigneeId;
        DueDate = dueDate;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Instant getDueDate() {
        return DueDate;
    }

    public void setDueDate(Instant dueDate) {
        DueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
