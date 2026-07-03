package com.jagha.collabflow.dto.task;

import com.jagha.collabflow.entity.TaskStatus;

import java.time.Instant;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long assigneeId;
    private String assigneeName;
    private Long boardId;
    private String boardName;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(Long id, String title, String description, TaskStatus status, Long assigneeId, String assigneeName, Long boardId, String boardName, Instant dueDate, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.boardId = boardId;
        this.boardName = boardName;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
