package com.jagha.gravix.controller;

import com.jagha.gravix.config.JwtUtil;
import com.jagha.gravix.config.SlaProperties;
import com.jagha.gravix.contoller.SlaController;
import com.jagha.gravix.entity.Board;
import com.jagha.gravix.entity.Task;
import com.jagha.gravix.entity.TaskStatus;
import com.jagha.gravix.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SlaController.class)
class SlaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private SlaProperties slaProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Board mockBoard;

    @BeforeEach
    void setUp() {
        mockBoard = new Board();
        mockBoard.setId(1L);
        when(slaProperties.getCriticalThresholdHours())
                .thenReturn(1);
    }

    private Task createTask(Long id, String title,
                            Instant dueDate, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setBoard(mockBoard);
        task.setStatus(status);
        task.setDueDate(dueDate);
        return task;
    }

    @Test
    @WithMockUser
    void getSlaStatus_NoTasks_ReturnsEmptyList()
            throws Exception {
        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getSlaStatus_BreachedTask_ReturnsBREACHED()
            throws Exception {
        Task breached = createTask(1L, "Overdue Task",
                Instant.now().minus(2, ChronoUnit.HOURS),
                TaskStatus.IN_PROGRESS);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(breached));

        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slaStatus")
                        .value("BREACHED"))
                .andExpect(jsonPath("$[0].taskId").value(1));
    }

    @Test
    @WithMockUser
    void getSlaStatus_OnTrackTask_ReturnsON_TRACK()
            throws Exception {
        Task onTrack = createTask(2L, "Future Task",
                Instant.now().plus(48, ChronoUnit.HOURS),
                TaskStatus.TODO);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(onTrack));

        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slaStatus")
                        .value("ON_TRACK"));
    }

    @Test
    @WithMockUser
    void getSlaStatus_WarningTask_ReturnsWARNING()
            throws Exception {
        Task warning = createTask(3L, "Urgent Task",
                Instant.now().plus(30, ChronoUnit.MINUTES),
                TaskStatus.IN_REVIEW);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(warning));

        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slaStatus")
                        .value("WARNING"));
    }

    @Test
    @WithMockUser
    void getSlaStatus_MultipleTasks_ReturnsSortedByDueDate()
            throws Exception {
        Task later = createTask(1L, "Later Task",
                Instant.now().plus(48, ChronoUnit.HOURS),
                TaskStatus.TODO);
        Task earlier = createTask(2L, "Earlier Task",
                Instant.now().minus(1, ChronoUnit.HOURS),
                TaskStatus.IN_PROGRESS);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(later, earlier));

        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].taskId").value(2))
                .andExpect(jsonPath("$[1].taskId").value(1));
    }

    @Test
    @WithMockUser
    void getBreachedTasks_NoBreaches_ReturnsEmptyList()
            throws Exception {
        Task onTrack = createTask(1L, "On Track",
                Instant.now().plus(24, ChronoUnit.HOURS),
                TaskStatus.TODO);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(onTrack));

        mockMvc.perform(get("/api/sla/breached"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getBreachedTasks_WithBreaches_ReturnsOnlyBreached()
            throws Exception {
        Task breached = createTask(1L, "Breached",
                Instant.now().minus(1, ChronoUnit.HOURS),
                TaskStatus.IN_PROGRESS);
        Task onTrack = createTask(2L, "On Track",
                Instant.now().plus(24, ChronoUnit.HOURS),
                TaskStatus.TODO);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(breached, onTrack));

        mockMvc.perform(get("/api/sla/breached"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].slaStatus")
                        .value("BREACHED"));
    }

    @Test
    @WithMockUser
    void getBreachedTasks_MultipleBreaches_ReturnsAll()
            throws Exception {
        Task breach1 = createTask(1L, "Breach 1",
                Instant.now().minus(1, ChronoUnit.HOURS),
                TaskStatus.IN_PROGRESS);
        Task breach2 = createTask(2L, "Breach 2",
                Instant.now().minus(2, ChronoUnit.HOURS),
                TaskStatus.IN_REVIEW);

        when(taskRepository.findActivetasksWithDueDate())
                .thenReturn(List.of(breach1, breach2));

        mockMvc.perform(get("/api/sla/breached"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getSlaStatus_Unauthenticated_Returns403()
            throws Exception {
        mockMvc.perform(get("/api/sla/status"))
                .andExpect(status().isForbidden());
    }
}