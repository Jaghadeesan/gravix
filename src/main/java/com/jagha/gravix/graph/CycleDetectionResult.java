package com.jagha.gravix.graph;

import lombok.Getter;

import java.util.List;

@Getter
public class CycleDetectionResult {

    private final boolean hasCycle;
    private final List<Long> cyclePath;

    public CycleDetectionResult(boolean hasCycle, List<Long> cyclePath) {
        this.hasCycle = hasCycle;
        this.cyclePath = cyclePath;
    }

    public static CycleDetectionResult cycleFound(List<Long> cyclePath) {
        return new CycleDetectionResult(true, cyclePath);
    }

    public static CycleDetectionResult noCycle() {
        return new CycleDetectionResult(false, List.of());
    }
}
