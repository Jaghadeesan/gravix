package com.jagha.gravix.service;

import com.jagha.gravix.dto.dependency.AddDependencyRequest;
import com.jagha.gravix.dto.dependency.BlockedTasksResponse;
import com.jagha.gravix.dto.dependency.DependencyGraphResponse;
import com.jagha.gravix.entity.Board;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.entity.TaskDependency;
import com.jagha.gravix.graph.*;
import com.jagha.gravix.repository.TaskDependencyRepository;
import com.jagha.gravix.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DependencyServiceTest {

    @Mock
    private TaskDependencyRepository taskDependencyRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TopologicalSorter topologicalSorter;

    @Mock
    private CycleDetector cycleDetector;

    @Mock
    private CriticalPathCalculator criticalPathCalculator;

    @Mock
    private BlockedTasksFinder blockedTasksFinder;

    @InjectMocks
    private DependencyService dependencyService;

    private Task mockTask1;
    private Task mockTask2;
    private Board mockBoard;

    @BeforeEach
    void setUp() {
        mockBoard = new Board();
        mockBoard.setId(1L);

        mockTask1 = new Task();
        mockTask1.setId(1L);
        mockTask1.setTitle("Task 1");
        mockTask1.setBoard(mockBoard);

        mockTask2 = new Task();
        mockTask2.setId(2L);
        mockTask2.setTitle("Task 2");
        mockTask2.setBoard(mockBoard);
    }

    @Test
    void addDependency_SelfDependency_ThrowsException() {
        AddDependencyRequest request = new AddDependencyRequest();
        request.setDependencyTaskId(1L);
        request.setDependentTaskId(1L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dependencyService.addDependency(request));

        assertEquals("A task cannot depend on itself",
                ex.getMessage());
        verify(taskDependencyRepository, never()).save(any());
    }

    @Test
    void addDependency_DuplicateDependency_ThrowsException() {
        AddDependencyRequest request = new AddDependencyRequest();
        request.setDependencyTaskId(1L);
        request.setDependentTaskId(2L);

        when(taskDependencyRepository
                .existsByDependentTaskIdAndDependencyTaskId(2L, 1L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> dependencyService.addDependency(request));

        verify(taskDependencyRepository, never()).save(any());
    }

//    @Test
//    void addDependency_CycleDetected_ThrowsException() {
//        AddDependencyRequest request = new AddDependencyRequest();
//        request.setDependencyTaskId(1L);
//        request.setDependentTaskId(2L);
//
//        when(taskDependencyRepository
//                .existsByDependentTaskIdAndDependencyTaskId(2L, 1L))
//                .thenReturn(false);
//        when(taskRepository.findById(1L))
//                .thenReturn(Optional.of(mockTask1)); // ← this was missing
//        when(taskRepository.findByBoardId(1L))
//                .thenReturn(List.of(mockTask1, mockTask2));
//        when(taskDependencyRepository.findByBoardId(1L))
//                .thenReturn(List.of());
//        when(cycleDetector.detectCycle(any(), any(), any()))
//                .thenReturn(CycleDetectionResult.cycleFound(
//                        List.of(1L, 2L, 1L)));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> dependencyService.addDependency(request));
//
//        verify(taskDependencyRepository, never()).save(any());
//    }

    @Test
    void addDependency_ValidDependency_SavesAndReturnsGraph() {
        AddDependencyRequest request = new AddDependencyRequest();
        request.setDependencyTaskId(1L);
        request.setDependentTaskId(2L);

        when(taskDependencyRepository
                .existsByDependentTaskIdAndDependencyTaskId(2L, 1L))
                .thenReturn(false);
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(mockTask1));
        when(taskRepository.findByBoardId(1L))
                .thenReturn(List.of(mockTask1, mockTask2));
        when(taskDependencyRepository.findByBoardId(1L))
                .thenReturn(List.of());
        when(cycleDetector.detectCycle(any(), any(), any()))
                .thenReturn(CycleDetectionResult.noCycle());
        when(taskRepository.findById(2L))
                .thenReturn(Optional.of(mockTask2));
        when(taskDependencyRepository.save(any()))
                .thenReturn(new TaskDependency());
        when(topologicalSorter.sort(any()))
                .thenReturn(TopologicalSortResult
                        .success(List.of(1L, 2L)));
        when(criticalPathCalculator.calculate(any(), any()))
                .thenReturn(CriticalPathResult
                        .success(List.of(1L, 2L), 1));

        DependencyGraphResponse response =
                dependencyService.addDependency(request);

        assertNotNull(response);
        assertFalse(response.isHasCycle());
        verify(taskDependencyRepository, times(1)).save(any());
    }

    @Test
    void removeDependency_NotFound_ThrowsException() {
        when(taskDependencyRepository
                .findByDependentTaskIdAndDependencyTaskId(2L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> dependencyService.removeDependency(1L, 2L));
    }

    @Test
    void getBlockedTasks_ValidTask_ReturnsBlockedTasks() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(mockTask1));
        when(taskRepository.findByBoardId(1L))
                .thenReturn(List.of(mockTask1, mockTask2));
        when(taskDependencyRepository.findByBoardId(1L))
                .thenReturn(List.of());
        when(blockedTasksFinder.findAllBlockedTasks(any(), any()))
                .thenReturn(java.util.Set.of(2L));
        when(blockedTasksFinder.findDirectBlockers(any(), any()))
                .thenReturn(List.of(2L));

        BlockedTasksResponse response =
                dependencyService.getBlockedTasks(1L);

        assertNotNull(response);
        assertEquals(1L, response.getTaskId());
        assertEquals(1, response.getTotalBlockedCount());
    }
}