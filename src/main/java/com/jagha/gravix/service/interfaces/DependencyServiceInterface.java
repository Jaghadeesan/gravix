package com.jagha.gravix.service.interfaces;

import com.jagha.gravix.dto.dependency.AddDependencyRequest;
import com.jagha.gravix.dto.dependency.BlockedTasksResponse;
import com.jagha.gravix.dto.dependency.DependencyGraphResponse;

public interface DependencyServiceInterface {

    DependencyGraphResponse addDependency(AddDependencyRequest request);

    void removeDependency(Long dependencyTaskId,Long dependentTaskId);

    DependencyGraphResponse getBoardDependencyGraph(Long boardId);

    BlockedTasksResponse getBlockedTasks(Long taskId);
}
