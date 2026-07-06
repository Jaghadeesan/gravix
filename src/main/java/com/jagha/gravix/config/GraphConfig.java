package com.jagha.gravix.config;

import com.jagha.gravix.graph.*;
import com.jagha.gravix.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {
    @Bean
    public TopologicalSorter topologicalSorter() {
        return new TopologicalSorter();
    }

    @Bean
    public CycleDetector cycleDetector() {
        return new CycleDetector();
    }

    @Bean
    public CriticalPathCalculator  criticalPathCalculator() {
        return new CriticalPathCalculator();
    }

    @Bean
    public BlockedTasksFinder blockedTasksFinder() {
        return new BlockedTasksFinder();
    }
}