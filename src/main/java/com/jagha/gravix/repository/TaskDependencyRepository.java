package com.jagha.gravix.repository;

import com.jagha.gravix.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Integer> {

    // All dependencies for a specific task
    // (tasks this task depends on)
    List<TaskDependency> findByDependentTaskId(Long dependentTaskId);

    // All tasks that depend on a specific task
    // (tasks blocked by this task)
    List<TaskDependency> findByDependencyTaskId(Long dependencyTaskId);

    // All dependencies within a board
    @Query("""
        SELECT td FROM TaskDependency td
        JOIN td.dependentTask t
        WHERE t.board.id = :boardId
    """)
    List<TaskDependency> findByBoardId(@Param("boardId") Long boardId);

    // Check if specific dependency already exists
    Optional<TaskDependency> findByDependentTaskIdAndDependencyTaskId(Long dependentTaskId, Long dependencyTaskId);

    // Check existence without fetching
    boolean existsByDependentTaskIdAndDependencyTaskId(Long dependentTaskId, Long dependencyTaskId);

}
