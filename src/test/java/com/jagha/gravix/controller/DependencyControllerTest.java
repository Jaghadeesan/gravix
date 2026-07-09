package com.jagha.gravix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagha.gravix.config.JwtUtil;
import com.jagha.gravix.contoller.DependencyController;
import com.jagha.gravix.dto.dependency.AddDependencyRequest;
import com.jagha.gravix.dto.dependency.BlockedTasksResponse;
import com.jagha.gravix.dto.dependency.DependencyGraphResponse;
import com.jagha.gravix.service.interfaces.DependencyServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DependencyController.class)
class DependencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DependencyServiceInterface dependencyService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void addDependency_ValidRequest_Returns200() throws Exception {
        AddDependencyRequest request = new AddDependencyRequest();
        request.setDependencyTaskId(1L);
        request.setDependentTaskId(2L);

        DependencyGraphResponse response = DependencyGraphResponse
                .builder()
                .boardId(1L)
                .topologicalOrder(List.of(1L, 2L))
                .criticalPath(List.of(1L, 2L))
                .criticalPathLength(1)
                .hasCycle(false)
                .cyclePath(List.of())
                .build();

        when(dependencyService.addDependency(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/dependencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.hasCycle").value(false));
    }

    @Test
    @WithMockUser
    void addDependency_CycleDetected_Returns400() throws Exception {
        AddDependencyRequest request = new AddDependencyRequest();
        request.setDependencyTaskId(2L);
        request.setDependentTaskId(1L);

        when(dependencyService.addDependency(any()))
                .thenThrow(new IllegalArgumentException(
                        "Adding this dependency would create a circular dependency"));

        mockMvc.perform(post("/api/dependencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Adding this dependency would create a circular dependency"));
    }

    @Test
    @WithMockUser
    void removeDependency_ValidRequest_Returns204() throws Exception {
        doNothing().when(dependencyService)
                .removeDependency(1L, 2L);

        mockMvc.perform(delete("/api/dependencies")
                        .with(csrf())
                        .param("dependencyTaskId", "1")
                        .param("dependentTaskId", "2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getBoardGraph_ValidBoardId_Returns200() throws Exception {
        DependencyGraphResponse response = DependencyGraphResponse
                .builder()
                .boardId(1L)
                .topologicalOrder(List.of(1L, 2L, 3L))
                .criticalPath(List.of(1L, 2L, 3L))
                .criticalPathLength(2)
                .hasCycle(false)
                .cyclePath(List.of())
                .build();

        when(dependencyService.getBoardDependencyGraph(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/dependencies/board/1/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criticalPathLength").value(2))
                .andExpect(jsonPath("$.topologicalOrder.length()").value(3));
    }

    @Test
    @WithMockUser
    void getBlockedTasks_ValidTaskId_Returns200() throws Exception {
        BlockedTasksResponse response = BlockedTasksResponse
                .builder()
                .taskId(1L)
                .allBlockedTaskIds(Set.of(2L, 3L))
                .directBlockerIds(List.of(2L))
                .totalBlockedCount(2)
                .build();

        when(dependencyService.getBlockedTasks(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/dependencies/tasks/1/blocked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBlockedCount").value(2))
                .andExpect(jsonPath("$.taskId").value(1L));
    }
}