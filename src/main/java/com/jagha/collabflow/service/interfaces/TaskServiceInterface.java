package com.jagha.collabflow.service.interfaces;

import com.jagha.collabflow.dto.task.TaskRequest;
import com.jagha.collabflow.dto.task.TaskResponse;
import com.jagha.collabflow.entity.TaskStatus;

import java.util.List;

public interface TaskServiceInterface {

    TaskResponse createTask(TaskRequest request);

    List<TaskResponse> getTasksByBoard(Long boardId);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, TaskRequest request);

    TaskResponse transitionTaskStatus(Long taskId, TaskStatus newStatus);

    void deleteTask(Long id);
}
