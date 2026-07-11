package com.jagha.gravix.repository;

import com.jagha.gravix.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    // All tasks for a board
    List<Task> findByBoardId(Long boardId);

    // Tasks assigned to a specific user
    List<Task> findByAssigneeId(Long assigneeId);

    // Tasks filtered by status within a board
    List<Task> findByBoardIdAndStatus(Long boardId,String status);

    // Find all non-DONE tasks that have a due date set
    @Query("""
    SELECT t FROM Task t
    WHERE t.status != com.jagha.gravix.entity.TaskStatus.DONE
    AND t.dueDate IS NOT NULL
    ORDER BY t.dueDate ASC
""")
    List<Task> findActivetasksWithDueDate();
}
