package com.jagha.gravix.service.interfaces;

import com.jagha.gravix.dto.task.TaskRequest;
import com.jagha.gravix.dto.task.TaskResponse;
import com.jagha.gravix.entity.TaskStatus;

import java.util.List;

public interface TaskServiceInterface {

    TaskResponse createTask(TaskRequest request);

    List<TaskResponse> getTasksByBoard(Long boardId);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, TaskRequest request);

    TaskResponse transitionTaskStatus(Long taskId, TaskStatus newStatus);

    void deleteTask(Long id);
}
