package com.jagha.gravix.graph;

import lombok.Getter;

import java.util.List;

@Getter
public class CriticalPathResult {

    private final boolean hasCycle;
    private final boolean hasPath;
    private final List<Long> criticalPath;
    private final int pathLength;

    public CriticalPathResult(boolean hasCycle, boolean hasPath, List<Long> criticalPath, int pathLength) {
        this.hasCycle = hasCycle;
        this.hasPath = hasPath;
        this.criticalPath = criticalPath;
        this.pathLength = pathLength;
    }

    public static CriticalPathResult withCycle() {
        return new CriticalPathResult(true, false, List.of(), 0);
    }

    public static CriticalPathResult noPath() {
        return new CriticalPathResult(false, false, List.of(), 0);
    }

    public static CriticalPathResult success(List<Long> criticalPath, Integer integer) {
        return new CriticalPathResult(false,  true, criticalPath, integer);
    }
}
