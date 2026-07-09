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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private BoardRespository boardRespository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthHelper authHelper;
    @Mock
    private EventPublisherInterface eventPublisher;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private Board mockBoard;
    private Task mockTask;
    private TaskRequest taskRequest;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("Test User");

        mockBoard = new Board();
        mockBoard.setId(1L);
        mockBoard.setName("Test Board");
        mockBoard.setOwner(mockUser);

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setStatus(TaskStatus.TODO);
        mockTask.setBoard(mockBoard);
        mockTask.setCreatedAt(Instant.now());
        mockTask.setUpdatedAt(Instant.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setBoardId(1L);
    }

    @Test
    void createTask_Success() {
        when(boardRespository.findById(1L)).thenReturn(Optional.of(mockBoard));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
        doNothing().when(eventPublisher).publishTaskEvent(any(TaskEvent.class));

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(eventPublisher, times(1)).publishTaskEvent(any(TaskEvent.class));
    }

    @Test
    void createTask_BoardNotFound_ThrowException() {
        when(boardRespository.findById(99L)).thenReturn(Optional.empty());
        taskRequest.setBoardId(99L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskRequest));

        assertEquals("Board not found", exception.getMessage());
    }

    @Test
    void getTaskById_NotFound_ThrowsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        taskService.deleteTask(1L);
        verify(taskRepository, times(1)).delete(mockTask);
    }
}
